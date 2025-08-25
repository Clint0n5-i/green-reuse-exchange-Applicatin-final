import { render, screen } from '@testing-library/react';
import Navbar from '../components/Navbar';

describe('Navbar', () => {
  it('renders the navigation links', () => {
    render(<Navbar />);
    expect(screen.getByText(/Home/i)).toBeInTheDocument();
    expect(screen.getByText(/Browse/i)).toBeInTheDocument();
    expect(screen.getByText(/About/i)).toBeInTheDocument();
  });
});
