import React, { useState, useEffect, useCallback } from 'react';
import ItemCard from '../components/ItemCard';
import './SearchItems.css';

const API = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const getHeaders = () => ({
  'Content-Type': 'application/json',
  ...(localStorage.getItem('token') && {
    Authorization: `Bearer ${localStorage.getItem('token')}`,
  }),
});

const CATEGORIES = [
  '', 'Electronics', 'Wallet/Purse', 'Keys', 'Bag/Backpack',
  'Clothing', 'Jewelry', 'Documents', 'Pet', 'Other',
];

const SearchItems = () => {
  const [items, setItems] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({
    keyword: '', type: '', category: '', location: '',
  });

  const fetchItems = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const queryParams = new URLSearchParams();
      queryParams.set('page', page);
      queryParams.set('size', 9);
      if (filters.keyword.trim())   queryParams.set('keyword',  filters.keyword.trim());
      if (filters.type)             queryParams.set('type',     filters.type);
      if (filters.category)         queryParams.set('category', filters.category);
      if (filters.location.trim())  queryParams.set('location', filters.location.trim());

      const url = `${API}/api/items/search?${queryParams.toString()}`;
      const res = await fetch(url, { headers: getHeaders() });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || `HTTP ${res.status}`);
      }

      const data = await res.json();

      // Spring Page<T> → { content: [...], totalElements: N }
      if (data && Array.isArray(data.content)) {
        setItems(data.content);
        setTotal(data.totalElements ?? data.content.length);
      } else if (Array.isArray(data)) {
        setItems(data);
        setTotal(data.length);
      } else {
        setItems([]);
        setTotal(0);
      }
    } catch (e) {
      console.error('Search error:', e);
      setError('Failed to load items. Please try again.');
      setItems([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  }, [page, filters]);

  useEffect(() => { fetchItems(); }, [fetchItems]);

  const handleFilterChange = (e) => {
    setFilters((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setPage(0);
  };

  const handleClear = () => {
    setFilters({ keyword: '', type: '', category: '', location: '' });
    setPage(0);
  };

  const hasFilters = filters.keyword || filters.type || filters.category || filters.location;
  const totalPages = Math.ceil(total / 9);

  return (
    <div className="search-page container">
      <h1 className="page-title">🔎 Search Items</h1>

      {/* Filters */}
      <div className="search-filters card">
        <input
          name="keyword"
          value={filters.keyword}
          onChange={handleFilterChange}
          placeholder="Search by keyword..."
          className="search-input"
        />
        <select name="type" value={filters.type} onChange={handleFilterChange}>
          <option value="">All Types</option>
          <option value="LOST">Lost</option>
          <option value="FOUND">Found</option>
        </select>
        <select name="category" value={filters.category} onChange={handleFilterChange}>
          {CATEGORIES.map((c) => (
            <option key={c} value={c}>{c || 'All Categories'}</option>
          ))}
        </select>
        <input
          name="location"
          value={filters.location}
          onChange={handleFilterChange}
          placeholder="Filter by location..."
        />
        {hasFilters && (
          <button className="btn btn-outline" onClick={handleClear} style={{ whiteSpace: 'nowrap' }}>
            ✕ Clear
          </button>
        )}
      </div>

      {/* Result count */}
      {!loading && !error && (
        <p className="results-count">{total} result{total !== 1 ? 's' : ''} found</p>
      )}

      {/* Error state */}
      {error && (
        <div className="no-results">
          <span>⚠️</span>
          <p>{error}</p>
          <button className="btn btn-primary" onClick={fetchItems} style={{ marginTop: '0.75rem' }}>
            Retry
          </button>
        </div>
      )}

      {/* Loading */}
      {loading && (
        <div className="search-loading">
          <div className="search-spinner" />
          <p>Searching...</p>
        </div>
      )}

      {/* Empty */}
      {!loading && !error && items.length === 0 && (
        <div className="no-results">
          <span>📭</span>
          <p>No items match your search. Try different keywords or clear filters.</p>
        </div>
      )}

      {/* Grid */}
      {!loading && !error && items.length > 0 && (
        <div className="grid-3">
          {items.map((item) => (
            <ItemCard key={item.id} item={item} />
          ))}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="pagination">
          <button
            className="btn btn-outline"
            disabled={page === 0}
            onClick={() => setPage((p) => p - 1)}
          >
            ← Previous
          </button>
          <span>Page {page + 1} of {totalPages}</span>
          <button
            className="btn btn-outline"
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((p) => p + 1)}
          >
            Next →
          </button>
        </div>
      )}
    </div>
  );
};

export default SearchItems;
