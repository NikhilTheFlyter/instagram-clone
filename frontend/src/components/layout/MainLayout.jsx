import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';

export default function MainLayout() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <main className="pt-[54px] max-w-[935px] mx-auto px-4">
        <Outlet />
      </main>
    </div>
  );
}
