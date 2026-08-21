import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { StatusBar } from './components/StatusBar';
import { NetworkPage } from './pages/NetworkPage';
import { TriagePage } from './pages/TriagePage';
import { BillingPage } from './pages/BillingPage';
import './styles/tokens.css';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <StatusBar />
      <main className="app-shell">
        <Routes>
          <Route path="/" element={<NetworkPage />} />
          <Route path="/triage" element={<TriagePage />} />
          <Route path="/billing" element={<BillingPage />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;
