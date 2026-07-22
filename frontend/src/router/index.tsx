import { createBrowserRouter, Navigate } from 'react-router-dom'
import { MainLayout } from '@/components/layout/MainLayout'
import { ProtectedRoute } from '@/components/layout/ProtectedRoute'
import Login from '@/pages/Login'
import Dashboard from '@/pages/Dashboard'
import Students from '@/pages/Students'
import Teachers from '@/pages/Teachers'
import Grades from '@/pages/Grades'
import AiAssistant from '@/pages/AiAssistant'
import NotFound from '@/pages/NotFound'

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <MainLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/dashboard" replace />,
      },
      {
        path: 'dashboard',
        element: <Dashboard />,
      },
      {
        path: 'students',
        element: (
          <ProtectedRoute allowedRoles={['ADMIN', 'TEACHER']}>
            <Students />
          </ProtectedRoute>
        ),
      },
      {
        path: 'teachers',
        element: (
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <Teachers />
          </ProtectedRoute>
        ),
      },
      {
        path: 'grades',
        element: <Grades />,
      },
      {
        path: 'ai-assistant',
        element: <AiAssistant />,
      },
    ],
  },
  {
    path: '*',
    element: <NotFound />,
  },
])
