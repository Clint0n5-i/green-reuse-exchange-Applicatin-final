import { render, screen } from '@testing-library/react';
import ItemCard from '../components/ItemCard';

describe('ItemCard', () => {
  it('renders item title and description', () => {
    const item = { title: 'Test Item', description: 'Test Description', location: 'Test Location' };
    render(<ItemCard item={item} />);
    expect(screen.getByText(/Test Item/i)).toBeInTheDocument();
    expect(screen.getByText(/Test Description/i)).toBeInTheDocument();
    expect(screen.getByText(/Test Location/i)).toBeInTheDocument();
  });
});
