import { Link } from "react-router";
import { useAuthStore } from "../stores/useAuthStore";

export function Header() {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);

  return (
    <header className="site-header">
      <Link to="/" className="site-header__brand">
        BookNest 📚
      </Link>

      <nav className="site-header__nav">
        {user ? (
          <>
            <span className="site-header__email">{user.email}</span>
            <button type="button" onClick={logout}>
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </nav>
    </header>
  );
}
