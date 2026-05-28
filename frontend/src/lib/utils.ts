import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

export function getLetterGradeColor(grade: string): string {
  const colors: Record<string, string> = {
    A: 'text-green-700 bg-green-100',
    B: 'text-blue-700 bg-blue-100',
    C: 'text-yellow-700 bg-yellow-100',
    D: 'text-orange-700 bg-orange-100',
    F: 'text-red-700 bg-red-100',
  }
  return colors[grade] ?? 'text-gray-700 bg-gray-100'
}

export function getScoreColor(score: number): string {
  if (score >= 90) return 'text-green-600'
  if (score >= 80) return 'text-blue-600'
  if (score >= 70) return 'text-yellow-600'
  if (score >= 60) return 'text-orange-600'
  return 'text-red-600'
}
