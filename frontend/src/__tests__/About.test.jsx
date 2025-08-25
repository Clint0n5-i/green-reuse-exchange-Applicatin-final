import { render, screen } from '@testing-library/react';
import About from '../pages/About';

describe('About', () => {
  it('renders the about page title', () => {
    render(<About />);
    expect(screen.getByText(/About/i)).toBeInTheDocument();
  });
});
