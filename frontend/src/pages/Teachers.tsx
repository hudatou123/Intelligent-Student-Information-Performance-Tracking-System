import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Plus, Search, Pencil, Trash2, AlertCircle, RefreshCw } from 'lucide-react'
import { teacherApi } from '@/api/teachers'
import type { Teacher } from '@/types'
import type { Column } from '@/components/ui/Table'
import { DataTable } from '@/components/ui/Table'
import { Pagination } from '@/components/ui/Pagination'
import { Button } from '@/components/ui/Button'
import { Input } from '@/components/ui/Input'
import { Modal, ConfirmDialog } from '@/components/ui/Modal'
import { Card, CardContent, CardHeader } from '@/components/ui/Card'
import { formatDate } from '@/lib/utils'

const teacherSchema = z.object({
  employeeId: z.string().min(1, 'Employee ID is required'),
  fullName: z.string().min(2, 'Full name must be at least 2 characters'),
  department: z.string().min(1, 'Department is required'),
  phone: z.string().min(1, 'Phone is required'),
})

type TeacherFormData = z.infer<typeof teacherSchema>

const PAGE_SIZE = 10

export default function Teachers() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [searchInput, setSearchInput] = useState('')
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingTeacher, setEditingTeacher] = useState<Teacher | null>(null)
  const [deletingTeacher, setDeletingTeacher] = useState<Teacher | null>(null)

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['teachers', page, search],
    queryFn: () => teacherApi.getAll(page, PAGE_SIZE, search),
  })

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm<TeacherFormData>({
    resolver: zodResolver(teacherSchema),
  })

  const createMutation = useMutation({
    mutationFn: teacherApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teachers'] })
      closeForm()
    },
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: TeacherFormData }) =>
      teacherApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teachers'] })
      closeForm()
    },
  })

  const deleteMutation = useMutation({
    mutationFn: teacherApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teachers'] })
      setDeletingTeacher(null)
    },
  })

  const openCreate = () => {
    reset()
    setEditingTeacher(null)
    setIsFormOpen(true)
  }

  const openEdit = (teacher: Teacher) => {
    setEditingTeacher(teacher)
    setValue('employeeId', teacher.employeeId)
    setValue('fullName', teacher.fullName)
    setValue('department', teacher.department)
    setValue('phone', teacher.phone)
    setIsFormOpen(true)
  }

  const closeForm = () => {
    setIsFormOpen(false)
    setEditingTeacher(null)
    reset()
  }

  const onSubmit = (formData: TeacherFormData) => {
    if (editingTeacher) {
      updateMutation.mutate({ id: editingTeacher.id, data: formData })
    } else {
      createMutation.mutate(formData)
    }
  }

  const handleSearch = () => {
    setSearch(searchInput)
    setPage(0)
  }

  const isSaving = createMutation.isPending || updateMutation.isPending
  const mutationError = createMutation.error ?? updateMutation.error

  const columns: Column<Teacher>[] = [
    {
      key: 'employeeId',
      header: 'Employee ID',
      sortable: true,
      render: (row) => (
        <span className="font-mono text-xs bg-gray-100 px-2 py-0.5 rounded">
          {row.employeeId}
        </span>
      ),
    },
    {
      key: 'fullName',
      header: 'Full Name',
      sortable: true,
      render: (row) => (
        <span className="font-medium text-gray-900">{row.fullName}</span>
      ),
    },
    {
      key: 'department',
      header: 'Department',
      sortable: true,
      render: (row) => (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-700">
          {row.department}
        </span>
      ),
    },
    {
      key: 'phone',
      header: 'Phone',
      render: (row) => <span className="text-sm">{row.phone}</span>,
    },
    {
      key: 'createdAt',
      header: 'Created',
      sortable: true,
      render: (row) => (
        <span className="text-sm text-gray-500">{formatDate(row.createdAt)}</span>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      headerClassName: 'text-right',
      className: 'text-right',
      render: (row) => (
        <div className="flex items-center justify-end gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={(e) => {
              e.stopPropagation()
              openEdit(row)
            }}
            className="h-7 px-2 text-gray-500 hover:text-blue-600"
          >
            <Pencil className="h-3.5 w-3.5" />
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={(e) => {
              e.stopPropagation()
              setDeletingTeacher(row)
            }}
            className="h-7 px-2 text-gray-500 hover:text-red-600"
          >
            <Trash2 className="h-3.5 w-3.5" />
          </Button>
        </div>
      ),
    },
  ]

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Teachers</h1>
          <p className="text-gray-500 text-sm mt-0.5">
            Manage teacher records ({data?.totalElements ?? 0} total)
          </p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4" />
          Add Teacher
        </Button>
      </div>

      <Card>
        <CardHeader className="pb-4">
          <div className="flex items-center gap-3">
            <div className="relative flex-1 max-w-xs">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400 pointer-events-none" />
              <input
                type="text"
                placeholder="Search by name, department..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                className="h-9 w-full pl-8 pr-3 rounded-md border border-gray-300 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <Button variant="outline" size="sm" onClick={handleSearch}>
              Search
            </Button>
            {search && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => {
                  setSearch('')
                  setSearchInput('')
                  setPage(0)
                }}
              >
                Clear
              </Button>
            )}
            <Button variant="ghost" size="sm" onClick={() => refetch()}>
              <RefreshCw className="h-4 w-4" />
            </Button>
          </div>
        </CardHeader>

        <CardContent className="p-0">
          {isError ? (
            <div className="p-8 text-center text-red-500 flex flex-col items-center gap-2">
              <AlertCircle className="h-8 w-8" />
              <p>Failed to load teachers.</p>
              <Button variant="outline" size="sm" onClick={() => refetch()}>
                Retry
              </Button>
            </div>
          ) : (
            <>
              <DataTable
                columns={columns}
                data={data?.content ?? []}
                isLoading={isLoading}
                emptyMessage="No teachers found. Add one to get started."
                keyExtractor={(row) => row.id}
              />
              {data && data.totalPages > 1 && (
                <div className="px-4 py-4 border-t border-gray-100">
                  <Pagination
                    currentPage={page}
                    totalPages={data.totalPages}
                    onPageChange={setPage}
                    totalElements={data.totalElements}
                    pageSize={PAGE_SIZE}
                  />
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>

      {/* Create / Edit Modal */}
      <Modal
        isOpen={isFormOpen}
        onClose={closeForm}
        title={editingTeacher ? 'Edit Teacher' : 'Add New Teacher'}
        description={
          editingTeacher
            ? `Editing ${editingTeacher.fullName}`
            : 'Fill in the details to add a new teacher.'
        }
        footer={
          <div className="flex justify-end gap-3">
            <Button variant="outline" onClick={closeForm} disabled={isSaving}>
              Cancel
            </Button>
            <Button onClick={handleSubmit(onSubmit)} loading={isSaving}>
              {editingTeacher ? 'Save Changes' : 'Add Teacher'}
            </Button>
          </div>
        }
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {mutationError && (
            <div className="flex items-start gap-2.5 p-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm">
              <AlertCircle className="h-4 w-4 mt-0.5 flex-shrink-0" />
              <span>
                {(mutationError as { response?: { data?: { message?: string } } }).response?.data?.message ??
                  'An error occurred. Please try again.'}
              </span>
            </div>
          )}

          <Input
            label="Employee ID"
            placeholder="EMP-001"
            disabled={!!editingTeacher}
            {...register('employeeId')}
            error={errors.employeeId?.message}
            helperText={editingTeacher ? 'Employee ID cannot be changed.' : undefined}
          />

          <Input
            label="Full Name"
            placeholder="Dr. Jane Smith"
            {...register('fullName')}
            error={errors.fullName?.message}
          />

          <Input
            label="Department"
            placeholder="Computer Science"
            {...register('department')}
            error={errors.department?.message}
          />

          <Input
            label="Phone"
            type="tel"
            placeholder="+1 (555) 000-0000"
            {...register('phone')}
            error={errors.phone?.message}
          />
        </form>
      </Modal>

      {/* Delete confirmation */}
      <ConfirmDialog
        isOpen={!!deletingTeacher}
        onClose={() => setDeletingTeacher(null)}
        onConfirm={() => deletingTeacher && deleteMutation.mutate(deletingTeacher.id)}
        title="Delete Teacher"
        description={`Are you sure you want to delete ${deletingTeacher?.fullName}? This action cannot be undone.`}
        confirmLabel="Delete"
        isLoading={deleteMutation.isPending}
        variant="danger"
      />
    </div>
  )
}
