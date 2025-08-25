import { render, screen } from '@testing-library/react';
import Login from '../pages/Login';

describe('Login', () => {
  it('renders the login form', () => {
    render(<Login />);
    expect(screen.getByText(/Login/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
  });
});
