import React from 'react';
import './Footer.css';

const Footer = () => (
  <footer className="footer">
    <div className="footer-inner">
      <p>© {new Date().getFullYear()} Lost &amp; Found Portal. Helping reunite people with their belongings.</p>
    </div>
  </footer>
);

export default Footer;
