import { Route, Routes } from "react-router";
import { BooksPage } from "./pages/BooksPage";
import { BookDetailsPage } from "./pages/BookDetailsPage";
import "./App.css";

function App() {
  return (
    <Routes>
      <Route path="/" element={<BooksPage />} />
      <Route path="/books/:id" element={<BookDetailsPage />} />
      <Route path="*" element={<BooksPage />} />
    </Routes>
  );
}

export default App;
