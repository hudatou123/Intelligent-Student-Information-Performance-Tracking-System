import { NavLink, useNavigate } from 'react-router-dom'
import {
  LayoutDashboard,
  Users,
  GraduationCap,
  BookOpen,
  Bot,
  LogOut,
  ChevronLeft,
  Award,
} from 'lucide-react'
import { cn } from '@/lib/utils'
import { useAuthStore } from '@/store/authStore'
import { RoleBadge } from '@/components/ui/Badge'
import { Button } from '@/components/ui/Button'

interface NavItem {
  label: string
  path: string
  icon: React.ElementType
  roles: string[]
}

const NAV_ITEMS: NavItem[] = [
  {
    label: 'Dashboard',
    path: '/dashboard',
    icon: LayoutDashboard,
    roles: ['ADMIN', 'TEACHER', 'STUDENT'],
  },
  {
    label: 'Students',
    path: '/students',
    icon: GraduationCap,
    roles: ['ADMIN', 'TEACHER'],
  },
  {
    label: 'Teachers',
    path: '/teachers',
    icon: Users,
    roles: ['ADMIN'],
  },
  {
    label: 'Grades',
    path: '/grades',
    icon: BookOpen,
    roles: ['ADMIN', 'TEACHER', 'STUDENT'],
  },
  {
    label: 'AI Assistant',
    path: '/ai-assistant',
    icon: Bot,
    roles: ['ADMIN', 'TEACHER', 'STUDENT'],
  },
]

interface SidebarProps {
  isCollapsed: boolean
  onToggle: () => void
}

export function Sidebar({ isCollapsed, onToggle }: SidebarProps) {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const filteredNav = NAV_ITEMS.filter(
    (item) => !user?.role || item.roles.includes(user.role)
  )

  return (
    <aside
      className={cn(
        'relative flex flex-col h-full bg-gray-900 text-white transition-all duration-300',
        isCollapsed ? 'w-16' : 'w-64'
      )}
    >
      {/* Logo */}
      <div className="flex items-center h-16 px-4 border-b border-gray-700 flex-shrink-0">
        <div className="flex items-center gap-3 min-w-0">
          <div className="flex-shrink-0 w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
            <Award className="h-5 w-5 text-white" />
          </div>
          {!isCollapsed && (
            <div className="min-w-0">
              <p className="text-sm font-bold text-white truncate">STS</p>
              <p className="text-xs text-gray-400 truncate">Student Tracking System</p>
            </div>
          )}
        </div>
      </div>

      {/* Toggle button */}
      <button
        onClick={onToggle}
        className={cn(
          'absolute -right-3 top-20 z-10 w-6 h-6 rounded-full',
          'bg-gray-700 border border-gray-600 text-gray-300',
          'flex items-center justify-center',
          'hover:bg-gray-600 transition-colors shadow-md'
        )}
        aria-label={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
      >
        <ChevronLeft
          className={cn(
            'h-3.5 w-3.5 transition-transform duration-300',
            isCollapsed && 'rotate-180'
          )}
        />
      </button>

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto py-4 px-2 space-y-1">
        {filteredNav.map((item) => {
          const Icon = item.icon
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all',
                  'hover:bg-gray-800',
                  isActive
                    ? 'bg-blue-600 text-white hover:bg-blue-700'
                    : 'text-gray-300 hover:text-white'
                )
              }
              title={isCollapsed ? item.label : undefined}
            >
              <Icon className="h-5 w-5 flex-shrink-0" />
              {!isCollapsed && <span className="truncate">{item.label}</span>}
            </NavLink>
          )
        })}
      </nav>

      {/* User info + Logout */}
      <div className="border-t border-gray-700 p-3 flex-shrink-0 space-y-2">
        {!isCollapsed && user && (
          <div className="px-2 py-2 rounded-lg bg-gray-800">
            <p className="text-sm font-medium text-white truncate">{user.fullName}</p>
            <p className="text-xs text-gray-400 truncate mb-1.5">@{user.username}</p>
            <RoleBadge role={user.role} />
          </div>
        )}
        <Button
          variant="ghost"
          size="sm"
          onClick={handleLogout}
          className={cn(
            'w-full text-gray-300 hover:text-white hover:bg-gray-800',
            isCollapsed ? 'justify-center px-2' : 'justify-start'
          )}
          title={isCollapsed ? 'Logout' : undefined}
        >
          <LogOut className="h-4 w-4 flex-shrink-0" />
          {!isCollapsed && <span>Logout</span>}
        </Button>
      </div>
    </aside>
  )
}
