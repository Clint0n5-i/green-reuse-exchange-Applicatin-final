import { render, screen } from '@testing-library/react';
import SearchBar from '../components/SearchBar';

describe('SearchBar', () => {
  it('renders search input and filter controls', () => {
    render(<SearchBar onSearch={() => {}} onFilterChange={() => {}} />);
    expect(screen.getByPlaceholderText(/Search items/i)).toBeInTheDocument();
    expect(screen.getByText(/Category/i)).toBeInTheDocument();
    expect(screen.getByText(/Location/i)).toBeInTheDocument();
  });
});
