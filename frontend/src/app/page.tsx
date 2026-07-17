'use client';

import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { LogIn, UserPlus, Key, Users } from 'lucide-react';
import api from '@/lib/api';
import { useRouter } from 'next/navigation';

export default function Home() {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [familyName, setFamilyName] = useState('');
  const [secretCode, setSecretCode] = useState('');
  const [role, setRole] = useState('PARENT');
  const [error, setError] = useState('');
  
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      if (isLogin) {
        const res = await api.post('/auth/login', { username, password });
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('username', res.data.username);
        router.push('/dashboard');
      } else {
        const res = await api.post('/auth/register', {
          username,
          password,
          familyName,
          secretCode,
          role
        });
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('username', res.data.username);
        router.push('/dashboard');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Hubo un error en la autenticación.');
    }
  };

  return (
    <div className="min-h-screen animated-bg flex items-center justify-center p-4">
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="glass-panel w-full max-w-md p-8 rounded-2xl relative overflow-hidden"
      >
        <div className="text-center mb-8">
          <div className="inline-block p-4 rounded-full bg-white/20 mb-4">
            <Users className="w-10 h-10 text-white" />
          </div>
          <h1 className="text-3xl font-bold text-white drop-shadow-md">
            Family Organizer
          </h1>
          <p className="text-white/80 mt-2">
            Organiza el día a día de tu familia.
          </p>
        </div>

        {error && (
          <div className="bg-red-500/20 border border-red-500/50 text-white p-3 rounded-xl mb-4 text-sm text-center">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4 relative z-10">
          <AnimatePresence mode="wait">
            {!isLogin && (
              <motion.div
                initial={{ opacity: 0, height: 0 }}
                animate={{ opacity: 1, height: 'auto' }}
                exit={{ opacity: 0, height: 0 }}
                className="space-y-4"
              >
                <div>
                  <label className="text-white text-sm ml-2">Nombre de la Familia (Opcional)</label>
                  <input
                    type="text"
                    value={familyName}
                    onChange={(e) => setFamilyName(e.target.value)}
                    className="w-full glass-input px-4 py-3 rounded-xl text-white placeholder-white/50"
                    placeholder="Ej. Familia Rodríguez"
                  />
                </div>
                <div>
                  <label className="text-white text-sm ml-2">Rol</label>
                  <select 
                    value={role} 
                    onChange={(e) => setRole(e.target.value)}
                    className="w-full glass-input px-4 py-3 rounded-xl text-white bg-transparent/20 [&>option]:text-black"
                  >
                    <option value="PARENT">Padre / Madre</option>
                    <option value="CHILD">Hijo / Hija</option>
                  </select>
                </div>
              </motion.div>
            )}
          </AnimatePresence>

          <div>
            <label className="text-white text-sm ml-2">Usuario</label>
            <input
              type="text"
              required
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full glass-input px-4 py-3 rounded-xl text-white placeholder-white/50 mt-1"
              placeholder="tu_usuario"
            />
          </div>

          <div>
            <label className="text-white text-sm ml-2">Contraseña</label>
            <input
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full glass-input px-4 py-3 rounded-xl text-white placeholder-white/50 mt-1"
              placeholder="••••••••"
            />
          </div>

          <AnimatePresence mode="wait">
            {!isLogin && (
              <motion.div
                initial={{ opacity: 0, height: 0 }}
                animate={{ opacity: 1, height: 'auto' }}
                exit={{ opacity: 0, height: 0 }}
              >
                <label className="text-white text-sm ml-2 flex items-center gap-1">
                  <Key className="w-3 h-3" /> Código Secreto Familiar
                </label>
                <input
                  type="text"
                  required={!isLogin}
                  value={secretCode}
                  onChange={(e) => setSecretCode(e.target.value)}
                  className="w-full glass-input px-4 py-3 rounded-xl text-white placeholder-white/50 mt-1"
                  placeholder="Ej. SECRETO-123"
                />
                <p className="text-xs text-white/70 mt-1 ml-2">
                  Usa el código de tu familia para unirte, o inventa uno nuevo para crear una familia.
                </p>
              </motion.div>
            )}
          </AnimatePresence>

          <button
            type="submit"
            className="w-full bg-white text-primary font-bold py-3 rounded-xl mt-6 hover:bg-white/90 transition-all flex justify-center items-center gap-2 shadow-lg hover:shadow-xl hover:-translate-y-1"
          >
            {isLogin ? <LogIn className="w-5 h-5" /> : <UserPlus className="w-5 h-5" />}
            {isLogin ? 'Iniciar Sesión' : 'Registrarse'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <button
            type="button"
            onClick={() => setIsLogin(!isLogin)}
            className="text-white hover:text-white/80 underline decoration-white/50 underline-offset-4 text-sm transition-all"
          >
            {isLogin 
              ? '¿No tienes cuenta? Regístrate o únete a tu familia' 
              : '¿Ya tienes cuenta? Inicia sesión'}
          </button>
        </div>
      </motion.div>
    </div>
  );
}
