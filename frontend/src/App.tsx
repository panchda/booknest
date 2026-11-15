import { Route, Routes } from "react-router";
import { AppLayout } from "./components/AppLayout";
import { BooksPage } from "./pages/BooksPage";
import { BookDetailsPage } from "./pages/BookDetailsPage";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import "./App.css";

function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path="/" element={<BooksPage />} />
        <Route path="/books/:id" element={<BookDetailsPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="*" element={<BooksPage />} />
      </Route>
    </Routes>
  );
}

export default App;
