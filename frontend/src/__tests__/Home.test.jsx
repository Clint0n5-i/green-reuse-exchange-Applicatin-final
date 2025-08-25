import { render, screen } from '@testing-library/react';
import Home from '../pages/Home';

describe('Home', () => {
  it('renders the home page title', () => {
    render(<Home />);
    expect(screen.getByText(/Home/i)).toBeInTheDocument();
  });
});
