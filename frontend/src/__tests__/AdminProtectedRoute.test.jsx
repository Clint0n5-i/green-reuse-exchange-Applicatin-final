import { render } from '@testing-library/react';
import AdminProtectedRoute from '../components/AdminProtectedRoute';

describe('AdminProtectedRoute', () => {
  it('renders children if admin', () => {
    const Child = () => <div>Admin Content</div>;
    render(<AdminProtectedRoute isAdmin={true}><Child /></AdminProtectedRoute>);
    expect(document.body.textContent).toContain('Admin Content');
  });

  it('does not render children if not admin', () => {
    const Child = () => <div>Admin Content</div>;
    render(<AdminProtectedRoute isAdmin={false}><Child /></AdminProtectedRoute>);
    expect(document.body.textContent).not.toContain('Admin Content');
  });
});
