import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from 'lucide-react'
import { cn } from '@/lib/utils'
import { Button } from './Button'

interface PaginationProps {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
  totalElements?: number
  pageSize?: number
  className?: string
}

export function Pagination({
  currentPage,
  totalPages,
  onPageChange,
  totalElements,
  pageSize,
  className,
}: PaginationProps) {
  if (totalPages <= 1) return null

  const start = pageSize ? currentPage * pageSize + 1 : null
  const end = pageSize ? Math.min((currentPage + 1) * pageSize, totalElements ?? 0) : null

  const getPageNumbers = () => {
    const delta = 2
    const range: number[] = []
    const left = Math.max(0, currentPage - delta)
    const right = Math.min(totalPages - 1, currentPage + delta)

    for (let i = left; i <= right; i++) {
      range.push(i)
    }

    if (range[0] > 0) {
      if (range[0] > 1) range.unshift(-1)
      range.unshift(0)
    }
    if (range[range.length - 1] < totalPages - 1) {
      if (range[range.length - 1] < totalPages - 2) range.push(-1)
      range.push(totalPages - 1)
    }

    return range
  }

  const pages = getPageNumbers()

  return (
    <div className={cn('flex items-center justify-between', className)}>
      {totalElements != null && pageSize != null ? (
        <p className="text-sm text-gray-500">
          Showing <span className="font-medium">{start}</span> to{' '}
          <span className="font-medium">{end}</span> of{' '}
          <span className="font-medium">{totalElements}</span> results
        </p>
      ) : (
        <div />
      )}

      <div className="flex items-center gap-1">
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(0)}
          disabled={currentPage === 0}
          aria-label="First page"
        >
          <ChevronsLeft className="h-4 w-4" />
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0}
          aria-label="Previous page"
        >
          <ChevronLeft className="h-4 w-4" />
        </Button>

        {pages.map((page, idx) =>
          page === -1 ? (
            <span key={`ellipsis-${idx}`} className="px-2 text-gray-400">
              …
            </span>
          ) : (
            <button
              key={page}
              onClick={() => onPageChange(page)}
              className={cn(
                'h-8 w-8 rounded-md text-sm font-medium transition-colors',
                page === currentPage
                  ? 'bg-blue-600 text-white'
                  : 'border border-gray-300 bg-white text-gray-700 hover:bg-gray-50'
              )}
              aria-current={page === currentPage ? 'page' : undefined}
            >
              {page + 1}
            </button>
          )
        )}

        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage >= totalPages - 1}
          aria-label="Next page"
        >
          <ChevronRight className="h-4 w-4" />
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(totalPages - 1)}
          disabled={currentPage >= totalPages - 1}
          aria-label="Last page"
        >
          <ChevronsRight className="h-4 w-4" />
        </Button>
      </div>
    </div>
  )
}
