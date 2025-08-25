import { render, screen } from '@testing-library/react';
import Dashboard from '../pages/Dashboard';

describe('Dashboard', () => {
  it('renders the dashboard title', () => {
    render(<Dashboard />);
    expect(screen.getByText(/Dashboard/i)).toBeInTheDocument();
  });
});
