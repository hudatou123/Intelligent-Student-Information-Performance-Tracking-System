import { HTMLAttributes } from 'react'
import { cn } from '@/lib/utils'

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info' | 'outline'
}

export function Badge({ className, variant = 'default', ...props }: BadgeProps) {
  const variants = {
    default: 'bg-gray-100 text-gray-700 border border-gray-200',
    success: 'bg-green-100 text-green-700 border border-green-200',
    warning: 'bg-yellow-100 text-yellow-700 border border-yellow-200',
    danger: 'bg-red-100 text-red-700 border border-red-200',
    info: 'bg-blue-100 text-blue-700 border border-blue-200',
    outline: 'border border-gray-300 text-gray-700 bg-transparent',
  }

  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold transition-colors',
        variants[variant],
        className
      )}
      {...props}
    />
  )
}

interface GradeBadgeProps {
  grade: string
  className?: string
}

export function GradeBadge({ grade, className }: GradeBadgeProps) {
  const gradeVariants: Record<string, BadgeProps['variant']> = {
    A: 'success',
    B: 'info',
    C: 'warning',
    D: 'warning',
    F: 'danger',
  }

  const variant = gradeVariants[grade] ?? 'default'

  return (
    <Badge variant={variant} className={cn('font-bold text-sm px-3 py-1', className)}>
      {grade}
    </Badge>
  )
}

interface RoleBadgeProps {
  role: string
  className?: string
}

export function RoleBadge({ role, className }: RoleBadgeProps) {
  const roleVariants: Record<string, BadgeProps['variant']> = {
    ADMIN: 'danger',
    TEACHER: 'info',
    STUDENT: 'success',
  }

  const variant = roleVariants[role] ?? 'default'

  return (
    <Badge variant={variant} className={className}>
      {role}
    </Badge>
  )
}
