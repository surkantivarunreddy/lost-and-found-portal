import React from 'react';
import { Link } from 'react-router-dom';
import './ItemCard.css';
 
const ItemCard = ({ item, onDelete, onResolve }) => {
  const formatDate = (dateStr) => {
    if (!dateStr) return 'Date unknown';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
    });
  };
 
  const isResolved = item.status === 'RESOLVED';
 
  return (
    <div className={`item-card ${isResolved ? 'item-card-resolved' : ''}`}>
      {item.imageUrl && (
        <div className="item-img">
          <img src={item.imageUrl} alt={item.title}
               onError={e => e.target.style.display = 'none'} />
        </div>
      )}
 
      {isResolved && (
        <div className="resolved-banner">✅ Reunited</div>
      )}
 
      <div className="item-body">
        <div className="item-badges">
          <span className={`badge badge-${item.type?.toLowerCase()}`}>{item.type}</span>
          <span className={`badge badge-${item.status?.toLowerCase()}`}>{item.status}</span>
        </div>
 
        <h3 className="item-title">{item.title}</h3>
 
        {item.description && (
          <p className="item-desc">
            {item.description.substring(0, 120)}
            {item.description.length > 120 ? '…' : ''}
          </p>
        )}
 
        <div className="item-meta">
          {item.category && <span>🏷️ {item.category}</span>}
          {item.location && <span>📍 {item.location}</span>}
          <span>📅 {formatDate(item.dateLostFound)}</span>
        </div>
 
        <div className="item-footer">
          <span className="item-reporter">By {item.reportedByName}</span>
          <div className="item-actions">
            <Link to={`/items/${item.id}`}
                  className="btn btn-outline"
                  style={{ fontSize: '0.8rem', padding: '0.3rem 0.8rem' }}>
              View Details
            </Link>
 
            {/* Mark as Resolved — only shown on Dashboard for ACTIVE items */}
            {onResolve && !isResolved && (
              <button
                className="btn btn-resolve"
                style={{ fontSize: '0.8rem', padding: '0.3rem 0.8rem' }}
                onClick={() => onResolve(item.id)}
                title="Mark as Reunited / Resolved">
                ✅ Resolved
              </button>
            )}
 
            {onDelete && (
              <button
                className="btn btn-danger"
                style={{ fontSize: '0.8rem', padding: '0.3rem 0.8rem' }}
                onClick={() => onDelete(item.id)}>
                Delete
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
 
export default ItemCard;