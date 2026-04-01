import React, { useEffect, useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import ItemCard from '../components/ItemCard';
import { useAuth } from '../context/AuthContext';
import { itemService } from '../services/itemService';
import './Dashboard.css';

const API = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const getHeaders = () => ({
  'Content-Type': 'application/json',
  Authorization: `Bearer ${localStorage.getItem('token')}`,
});

const Dashboard = () => {
  const { user } = useAuth();
  const [myItems, setMyItems]             = useState([]);
  const [loading, setLoading]             = useState(true);
  const [activeTab, setActiveTab]         = useState('items');
  const [unread, setUnread]               = useState(0);
  const [conversations, setConversations] = useState([]);
  const [activeConvo, setActiveConvo]     = useState(null);
  const [messages, setMessages]           = useState([]);
  const [msgLoading, setMsgLoading]       = useState(false);
  const [replyText, setReplyText]         = useState('');
  const [sending, setSending]             = useState(false);
  const [imageFile, setImageFile]         = useState(null);
  const [imagePreview, setImagePreview]   = useState(null);
  const [uploadingImg, setUploadingImg]   = useState(false);
  const [locationText, setLocationText]   = useState('');
  const [fetchingLoc, setFetchingLoc]     = useState(false);
  const imageInputRef                     = useRef(null);
  const messagesEndRef                    = useRef(null);

  useEffect(() => {
    Promise.all([
      itemService.getMyItems(),
      axios.get(`${API}/api/messages/unread-count`, { headers: getHeaders() }),
    ]).then(([itemsRes, unreadRes]) => {
      setMyItems(itemsRes.data || []);
      setUnread(unreadRes.data.count || 0);
    }).catch(console.error).finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (activeTab === 'messages') loadConversations();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeTab]);

  const loadConversations = async () => {
    try {
      const res  = await axios.get(`${API}/api/messages`, { headers: getHeaders() });
      const msgs = res.data || [];
      const convMap = {};
      msgs.forEach((msg) => {
        const isMine    = msg.senderId === user.id;
        const otherId   = isMine ? msg.receiverId : msg.senderId;
        const otherName = isMine ? msg.receiverName : msg.senderName;
        if (!convMap[otherId]) {
          convMap[otherId] = { userId: otherId, name: otherName, lastMsg: '', lastTime: null, unread: 0 };
        }
        if (!convMap[otherId].lastTime || new Date(msg.sentAt) > new Date(convMap[otherId].lastTime)) {
          convMap[otherId].lastMsg  = msg.imageUrl ? '📷 Image' : msg.locationText ? '📍 Location' : (msg.content || '');
          convMap[otherId].lastTime = msg.sentAt;
        }
        if (!isMine && !msg.isRead) convMap[otherId].unread++;
      });
      const list = Object.values(convMap).sort((a, b) => new Date(b.lastTime) - new Date(a.lastTime));
      setConversations(list);
      if (list.length > 0 && !activeConvo) openConversation(list[0]);
    } catch (e) { console.error(e); }
  };

  const openConversation = async (convo) => {
    setActiveConvo(convo);
    setMsgLoading(true);
    setReplyText('');
    setImageFile(null);
    setImagePreview(null);
    setLocationText('');
    try {
      const res  = await axios.get(`${API}/api/messages/conversation/${convo.userId}`, { headers: getHeaders() });
      const msgs = res.data || [];
      setMessages(msgs);
      const unreadMsgs = msgs.filter((m) => m.receiverId === user.id && !m.isRead);
      await Promise.all(unreadMsgs.map((m) =>
        axios.patch(`${API}/api/messages/${m.id}/read`, {}, { headers: getHeaders() })
      ));
      if (unreadMsgs.length > 0) {
        setUnread((prev) => Math.max(0, prev - unreadMsgs.length));
        setConversations((prev) => prev.map((c) => c.userId === convo.userId ? { ...c, unread: 0 } : c));
      }
    } catch (e) { console.error(e); }
    finally { setMsgLoading(false); }
  };

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleImageSelect = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) { alert('Please select an image file.'); return; }
    if (file.size > 5 * 1024 * 1024)    { alert('Image must be under 5 MB.');     return; }
    setImageFile(file);
    setImagePreview(URL.createObjectURL(file));
  };

  const clearImage = () => {
    setImageFile(null);
    setImagePreview(null);
    if (imageInputRef.current) imageInputRef.current.value = '';
  };

  const handleShareLocation = () => {
    if (!navigator.geolocation) { alert('Geolocation is not supported by your browser.'); return; }
    setFetchingLoc(true);
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const { latitude, longitude } = pos.coords;
        setLocationText(`${latitude.toFixed(5)}, ${longitude.toFixed(5)}`);
        setFetchingLoc(false);
      },
      (err) => { console.error(err); alert('Could not get location. Allow location access.'); setFetchingLoc(false); },
      { timeout: 10000 }
    );
  };

  const handleSendReply = async (e) => {
    e?.preventDefault();
    if (!activeConvo) return;
    const hasText  = !!replyText.trim();
    const hasImage = !!imageFile;
    const hasLoc   = !!locationText;
    if (!hasText && !hasImage && !hasLoc) return;

    setSending(true);
    const textToSend = replyText.trim();
    setReplyText('');

    try {
      let newMsg;
      if (hasImage) {
        setUploadingImg(true);
        const formData = new FormData();
        formData.append('receiverId', activeConvo.userId);
        if (textToSend) formData.append('content', textToSend);
        if (hasLoc)     formData.append('locationText', locationText);
        formData.append('image', imageFile);
        const res = await axios.post(`${API}/api/messages/with-image`, formData, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });
        newMsg = res.data;
        setUploadingImg(false);
        clearImage();
      } else {
        const body = { receiverId: activeConvo.userId, content: textToSend || `📍 ${locationText}` };
        if (hasLoc && textToSend) body.locationText = locationText;
        const res = await axios.post(`${API}/api/messages`, body, { headers: getHeaders() });
        newMsg = res.data;
      }
      if (hasLoc) setLocationText('');
      setMessages((prev) => [...prev, newMsg]);
      const preview = hasImage ? '📷 Image' : hasLoc ? `📍 ${locationText}` : textToSend;
      setConversations((prev) => prev.map((c) =>
        c.userId === activeConvo.userId ? { ...c, lastMsg: preview, lastTime: new Date().toISOString() } : c
      ));
    } catch (e) {
      console.error(e);
      alert('Failed to send message.');
      setReplyText(textToSend);
    } finally { setSending(false); setUploadingImg(false); }
  };

  const handleDelete  = async (id) => {
    if (!window.confirm('Delete this item?')) return;
    try { await itemService.delete(id); setMyItems((prev) => prev.filter((i) => i.id !== id)); }
    catch (e) { alert('Failed to delete item.'); }
  };

  const handleResolve = async (id) => {
    if (!window.confirm('Mark this item as Resolved?')) return;
    try {
      await itemService.update(id, { status: 'RESOLVED' });
      setMyItems((prev) => prev.map((i) => i.id === id ? { ...i, status: 'RESOLVED' } : i));
    } catch (e) { alert('Failed to update status.'); }
  };

  const formatTime = (t) => {
    if (!t) return '';
    const d = new Date(t);
    const isToday = d.toDateString() === new Date().toDateString();
    return isToday
      ? d.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' })
      : d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short' });
  };

  const lostItems     = myItems.filter((i) => i.type === 'LOST');
  const foundItems    = myItems.filter((i) => i.type === 'FOUND');
  const resolvedItems = myItems.filter((i) => i.status === 'RESOLVED');

  const renderBubble = (msg, i) => {
    const isMine = msg.senderId === user.id;
    const showDate = i === 0 ||
      new Date(msg.sentAt).toDateString() !== new Date(messages[i - 1].sentAt).toDateString();
    return (
      <React.Fragment key={msg.id}>
        {showDate && (
          <div className="chat-date-divider">
            {new Date(msg.sentAt).toLocaleDateString('en-IN', { weekday: 'long', day: 'numeric', month: 'long' })}
          </div>
        )}
        <div className={`bubble-row ${isMine ? 'row-mine' : 'row-theirs'}`}>
          {!isMine && <div className="bubble-avatar">{activeConvo.name.charAt(0).toUpperCase()}</div>}
          <div className={`bubble ${isMine ? 'bubble-mine' : 'bubble-theirs'}`}>
            {msg.itemTitle && <div className="bubble-item-ref">📦 re: {msg.itemTitle}</div>}
            {msg.imageUrl && (
              <div className="bubble-img">
                <img src={msg.imageUrl} alt="attachment"
                  onClick={() => window.open(msg.imageUrl, '_blank')} style={{ cursor: 'pointer' }} />
              </div>
            )}
            {msg.locationText && (
              <div className="bubble-location">
                <a href={`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(msg.locationText)}`}
                  target="_blank" rel="noopener noreferrer">📍 {msg.locationText}</a>
              </div>
            )}
            {msg.content && <p>{msg.content}</p>}
            <div className="bubble-meta">
              <span className="bubble-time">
                {new Date(msg.sentAt).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' })}
              </span>
              {isMine && (
                <span className={`bubble-ticks ${msg.isRead ? 'read' : ''}`}>
                  {msg.isRead ? '✓✓' : '✓'}
                </span>
              )}
            </div>
          </div>
        </div>
      </React.Fragment>
    );
  };

  return (
    <div className="dashboard container">
      <div className="dash-header">
        <div>
          <h1>👋 Welcome, {user?.name}!</h1>
          <p className="dash-email">{user?.email}</p>
        </div>
        <div className="dash-actions">
          <Link to="/report-lost" className="btn btn-outline">+ Report Lost</Link>
          <Link to="/report-found" className="btn btn-primary">+ Report Found</Link>
        </div>
      </div>

      <div className="dash-stats">
        <div className="dash-stat"><strong>{myItems.length}</strong><span>Total Reports</span></div>
        <div className="dash-stat"><strong>{lostItems.length}</strong><span>Lost Items</span></div>
        <div className="dash-stat"><strong>{foundItems.length}</strong><span>Found Items</span></div>
        <div className="dash-stat dash-stat-green"><strong>{resolvedItems.length}</strong><span>✅ Reunited</span></div>
        <div className="dash-stat"><strong>{unread}</strong><span>Unread Messages</span></div>
      </div>

      <div className="dash-tabs">
        <button className={activeTab === 'items' ? 'active' : ''} onClick={() => setActiveTab('items')}>
          My Items ({myItems.length})
        </button>
        <button className={activeTab === 'messages' ? 'active' : ''} onClick={() => setActiveTab('messages')}>
          Messages {unread > 0 && <span className="badge-dot">{unread}</span>}
        </button>
      </div>

      {activeTab === 'items' && (
        loading ? <p className="loading-text">Loading...</p> :
        myItems.length === 0 ? (
          <div className="dash-empty">
            <p>You haven't reported any items yet.</p>
            <Link to="/report-lost" className="btn btn-primary">Report your first item</Link>
          </div>
        ) : (
          <div className="grid-3">
            {myItems.map((item) => (
              <ItemCard key={item.id} item={item}
                onDelete={handleDelete}
                onResolve={item.status !== 'RESOLVED' ? handleResolve : null} />
            ))}
          </div>
        )
      )}

      {activeTab === 'messages' && (
        <div className="messages-layout">
          <div className="convo-list">
            <div className="convo-list-header">💬 Conversations</div>
            {conversations.length === 0 ? (
              <div className="convo-empty">
                <p>No conversations yet.</p>
                <p>Browse items and click <strong>"Send Message"</strong> to start chatting.</p>
              </div>
            ) : (
              conversations.map((convo) => (
                <div key={convo.userId}
                  className={`convo-item ${activeConvo?.userId === convo.userId ? 'convo-active' : ''}`}
                  onClick={() => openConversation(convo)}>
                  <div className="convo-avatar">{convo.name.charAt(0).toUpperCase()}</div>
                  <div className="convo-info">
                    <div className="convo-name-row">
                      <span className="convo-name">{convo.name}</span>
                      {convo.unread > 0 && <span className="convo-unread-badge">{convo.unread}</span>}
                    </div>
                    <div className="convo-preview">
                      {convo.lastMsg?.substring(0, 40)}{convo.lastMsg?.length > 40 ? '...' : ''}
                    </div>
                  </div>
                  <div className="convo-time">{formatTime(convo.lastTime)}</div>
                </div>
              ))
            )}
          </div>

          <div className="chat-window">
            {!activeConvo ? (
              <div className="chat-placeholder"><span>💬</span><p>Select a conversation to start chatting</p></div>
            ) : (
              <>
                <div className="chat-header">
                  <div className="chat-header-avatar">{activeConvo.name.charAt(0).toUpperCase()}</div>
                  <div className="chat-header-info">
                    <div className="chat-header-name">{activeConvo.name}</div>
                    <div className="chat-header-sub">Lost &amp; Found Portal</div>
                  </div>
                </div>

                <div className="chat-messages">
                  {msgLoading ? (
                    <p className="loading-text">Loading messages...</p>
                  ) : messages.length === 0 ? (
                    <div className="chat-no-msgs"><span>💬</span><p>No messages yet. Start the conversation!</p></div>
                  ) : (
                    <>{messages.map((msg, i) => renderBubble(msg, i))}<div ref={messagesEndRef} /></>
                  )}
                </div>

                {imagePreview && (
                  <div className="chat-img-preview">
                    <img src={imagePreview} alt="preview" />
                    <button className="chat-img-clear" onClick={clearImage}>✕</button>
                  </div>
                )}

                {locationText && (
                  <div className="chat-loc-preview">
                    <span>📍 {locationText}</span>
                    <button className="chat-img-clear" onClick={() => setLocationText('')}>✕</button>
                  </div>
                )}

                <div className="chat-input-area">
                  <div className="chat-attach-btns">
                    <input type="file" accept="image/*" ref={imageInputRef}
                      onChange={handleImageSelect} style={{ display: 'none' }} />
                    <button type="button" className="chat-attach-btn" title="Send image"
                      onClick={() => imageInputRef.current?.click()}>📷</button>
                    <button type="button"
                      className={`chat-attach-btn${locationText ? ' active' : ''}`}
                      title="Share location" onClick={handleShareLocation} disabled={fetchingLoc}>
                      {fetchingLoc ? '⏳' : '📍'}
                    </button>
                  </div>
                  <textarea
                    className="chat-textarea"
                    placeholder={`Message ${activeConvo.name}... (Enter to send)`}
                    value={replyText} rows={2}
                    onChange={(e) => setReplyText(e.target.value)}
                    onKeyDown={(e) => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); handleSendReply(e); } }}
                  />
                  <button type="button" className="chat-send-btn"
                    disabled={sending || (!replyText.trim() && !imageFile && !locationText)}
                    onClick={handleSendReply}>
                    {sending || uploadingImg ? '⏳' : '➤'}
                  </button>
                </div>
                <p className="chat-hint">Enter to send · Shift+Enter for newline · 📷 image · 📍 location</p>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
