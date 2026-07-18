'use client';

import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Plus, Trash2, GripVertical } from 'lucide-react';
import api from '@/lib/api';

interface Task {
  id: number;
  title: string;
  description: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
}

const columns: { id: Task['status']; label: string; color: string; bg: string }[] = [
  { id: 'TODO', label: '📋 Por hacer', color: '#6366f1', bg: 'rgba(99,102,241,0.1)' },
  { id: 'IN_PROGRESS', label: '🔄 En progreso', color: '#f59e0b', bg: 'rgba(245,158,11,0.1)' },
  { id: 'DONE', label: '✅ Hecho', color: '#10b981', bg: 'rgba(16,185,129,0.1)' },
];

export default function KanbanBoard() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [newTitle, setNewTitle] = useState('');
  const [newDesc, setNewDesc] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [dragging, setDragging] = useState<Task | null>(null);

  useEffect(() => {
    fetchTasks();
  }, []);

  const fetchTasks = async () => {
    try {
      const res = await api.get('/tasks');
      setTasks(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  const createTask = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim()) return;
    try {
      const res = await api.post('/tasks', { title: newTitle, description: newDesc, status: 'TODO' });
      setTasks(prev => [...prev, res.data]);
      setNewTitle('');
      setNewDesc('');
      setShowForm(false);
    } catch (e) {
      console.error(e);
    }
  };

  const moveTask = async (task: Task, newStatus: Task['status']) => {
    if (task.status === newStatus) return;
    try {
      const res = await api.patch(`/tasks/${task.id}/status?status=${newStatus}`);
      setTasks(prev => prev.map(t => t.id === task.id ? res.data : t));
    } catch (e) {
      console.error(e);
    }
  };

  const deleteTask = async (taskId: number) => {
    try {
      await api.delete(`/tasks/${taskId}`);
      setTasks(prev => prev.filter(t => t.id !== taskId));
    } catch (e) {
      console.error(e);
    }
  };

  const handleDrop = (e: React.DragEvent, status: Task['status']) => {
    e.preventDefault();
    if (dragging) moveTask(dragging, status);
    setDragging(null);
  };

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <p className="text-slate-400 text-sm">{tasks.length} tareas en total</p>
        <button
          onClick={() => setShowForm(!showForm)}
          className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold text-white transition-all hover:-translate-y-0.5"
          style={{ background: 'linear-gradient(135deg,#6366f1,#8b5cf6)' }}
        >
          <Plus className="w-4 h-4" />
          Nueva tarea
        </button>
      </div>

      {/* New task form */}
      {showForm && (
        <motion.form
          initial={{ opacity: 0, height: 0 }}
          animate={{ opacity: 1, height: 'auto' }}
          onSubmit={createTask}
          className="mb-6 p-4 rounded-2xl space-y-3"
          style={{ background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.1)' }}
        >
          <input
            type="text"
            value={newTitle}
            onChange={e => setNewTitle(e.target.value)}
            placeholder="Título de la tarea..."
            className="w-full px-4 py-2.5 rounded-xl text-white placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            style={{ background: 'rgba(255,255,255,0.07)', border: '1px solid rgba(255,255,255,0.1)' }}
            autoFocus
          />
          <input
            type="text"
            value={newDesc}
            onChange={e => setNewDesc(e.target.value)}
            placeholder="Descripción (opcional)..."
            className="w-full px-4 py-2.5 rounded-xl text-white placeholder-slate-500 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            style={{ background: 'rgba(255,255,255,0.07)', border: '1px solid rgba(255,255,255,0.1)' }}
          />
          <div className="flex gap-2">
            <button type="submit" className="px-4 py-2 rounded-xl text-sm font-semibold text-white" style={{ background: '#6366f1' }}>
              Agregar
            </button>
            <button type="button" onClick={() => setShowForm(false)} className="px-4 py-2 rounded-xl text-sm font-semibold text-slate-400 hover:text-white" style={{ background: 'rgba(255,255,255,0.07)' }}>
              Cancelar
            </button>
          </div>
        </motion.form>
      )}

      {/* Columns */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {columns.map(col => (
          <div
            key={col.id}
            onDragOver={e => e.preventDefault()}
            onDrop={e => handleDrop(e, col.id)}
            className="rounded-2xl p-4 min-h-[300px]"
            style={{ background: col.bg, border: `1px solid ${col.color}30` }}
          >
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-sm font-bold" style={{ color: col.color }}>{col.label}</h3>
              <span className="text-xs px-2 py-0.5 rounded-full font-semibold" style={{ background: `${col.color}20`, color: col.color }}>
                {tasks.filter(t => t.status === col.id).length}
              </span>
            </div>

            <div className="space-y-2">
              {tasks.filter(t => t.status === col.id).map(task => (
                <motion.div
                  key={task.id}
                  layout
                  draggable
                  onDragStart={() => setDragging(task)}
                  className="group p-3 rounded-xl cursor-grab active:cursor-grabbing"
                  style={{ background: 'rgba(15,23,42,0.7)', border: '1px solid rgba(255,255,255,0.08)' }}
                  whileHover={{ scale: 1.02 }}
                >
                  <div className="flex items-start gap-2">
                    <GripVertical className="w-4 h-4 text-slate-600 mt-0.5 shrink-0" />
                    <div className="flex-1 min-w-0">
                      <p className="text-white text-sm font-medium truncate">{task.title}</p>
                      {task.description && (
                        <p className="text-slate-400 text-xs mt-0.5 truncate">{task.description}</p>
                      )}
                    </div>
                    <button
                      onClick={() => deleteTask(task.id)}
                      className="opacity-0 group-hover:opacity-100 text-slate-600 hover:text-red-400 transition-all shrink-0"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                  {/* Quick move buttons */}
                  <div className="flex gap-1 mt-2 opacity-0 group-hover:opacity-100 transition-all">
                    {columns.filter(c => c.id !== col.id).map(c => (
                      <button
                        key={c.id}
                        onClick={() => moveTask(task, c.id)}
                        className="text-xs px-2 py-0.5 rounded-lg transition-all hover:scale-105"
                        style={{ background: `${c.color}20`, color: c.color }}
                      >
                        → {c.label.split(' ')[1]}
                      </button>
                    ))}
                  </div>
                </motion.div>
              ))}

              {tasks.filter(t => t.status === col.id).length === 0 && (
                <div className="text-center py-8 text-slate-600 text-sm">
                  Arrastrá tareas aquí
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
