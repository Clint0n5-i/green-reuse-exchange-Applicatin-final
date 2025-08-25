import { render } from '@testing-library/react';
import ProtectedRoute from '../components/ProtectedRoute';

describe('ProtectedRoute', () => {
  it('renders children if authenticated', () => {
    const Child = () => <div>Protected Content</div>;
    render(<ProtectedRoute isAuthenticated={true}><Child /></ProtectedRoute>);
    // Should render the child content
    expect(document.body.textContent).toContain('Protected Content');
  });

  it('does not render children if not authenticated', () => {
    const Child = () => <div>Protected Content</div>;
    render(<ProtectedRoute isAuthenticated={false}><Child /></ProtectedRoute>);
    // Should not render the child content
    expect(document.body.textContent).not.toContain('Protected Content');
  });
});
