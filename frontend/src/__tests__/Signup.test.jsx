import { render, screen } from '@testing-library/react';
import Signup from '../pages/Signup';

describe('Signup', () => {
  it('renders the signup form', () => {
    render(<Signup />);
    expect(screen.getByText(/Sign Up/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
  });
});
