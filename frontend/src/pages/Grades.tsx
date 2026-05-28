import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Plus, Pencil, Trash2, AlertCircle, RefreshCw, Filter } from 'lucide-react'
import { gradeApi } from '@/api/grades'
import { studentApi } from '@/api/students'
import type { Grade } from '@/types'
import type { Column } from '@/components/ui/Table'
import { DataTable } from '@/components/ui/Table'
import { Pagination } from '@/components/ui/Pagination'
import { Button } from '@/components/ui/Button'
import { Input, Select } from '@/components/ui/Input'
import { Modal, ConfirmDialog } from '@/components/ui/Modal'
import { Card, CardContent, CardHeader } from '@/components/ui/Card'
import { GradeBadge } from '@/components/ui/Badge'
import { formatDate, getScoreColor } from '@/lib/utils'

const gradeSchema = z.object({
  studentId: z.coerce.number().min(1, 'Student is required'),
  courseName: z.string().min(1, 'Course name is required'),
  semester: z.string().min(1, 'Semester is required'),
  assignmentScore: z.coerce
    .number()
    .min(0, 'Score must be 0-100')
    .max(100, 'Score must be 0-100'),
  midtermScore: z.coerce
    .number()
    .min(0, 'Score must be 0-100')
    .max(100, 'Score must be 0-100'),
  finalScore: z.coerce
    .number()
    .min(0, 'Score must be 0-100')
    .max(100, 'Score must be 0-100'),
})

type GradeFormData = z.infer<typeof gradeSchema>

const SEMESTERS = [
  'Spring 2024',
  'Summer 2024',
  'Fall 2024',
  'Spring 2025',
  'Summer 2025',
  'Fall 2025',
  'Spring 2026',
]

const PAGE_SIZE = 10

export default function Grades() {
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)
  const [filterStudentId, setFilterStudentId] = useState<number | null>(null)
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingGrade, setEditingGrade] = useState<Grade | null>(null)
  const [deletingGrade, setDeletingGrade] = useState<Grade | null>(null)

  // Fetch all students for the filter dropdown and form select
  const { data: studentsData } = useQuery({
    queryKey: ['students', 'all'],
    queryFn: () => studentApi.getAll(0, 200),
  })

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['grades', page, filterStudentId],
    queryFn: () =>
      filterStudentId
        ? gradeApi.getByStudent(filterStudentId, page, PAGE_SIZE)
        : gradeApi.getAll(page, PAGE_SIZE),
  })

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm<GradeFormData>({
    resolver: zodResolver(gradeSchema),
  })

  const createMutation = useMutation({
    mutationFn: gradeApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['grades'] })
      closeForm()
    },
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<GradeFormData> }) =>
      gradeApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['grades'] })
      closeForm()
    },
  })

  const deleteMutation = useMutation({
    mutationFn: gradeApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['grades'] })
      setDeletingGrade(null)
    },
  })

  const openCreate = () => {
    reset()
    setEditingGrade(null)
    setIsFormOpen(true)
  }

  const openEdit = (grade: Grade) => {
    setEditingGrade(grade)
    setValue('studentId', grade.studentId)
    setValue('courseName', grade.courseName)
    setValue('semester', grade.semester)
    setValue('assignmentScore', grade.assignmentScore)
    setValue('midtermScore', grade.midtermScore)
    setValue('finalScore', grade.finalScore)
    setIsFormOpen(true)
  }

  const closeForm = () => {
    setIsFormOpen(false)
    setEditingGrade(null)
    reset()
  }

  const onSubmit = (formData: GradeFormData) => {
    if (editingGrade) {
      updateMutation.mutate({ id: editingGrade.id, data: formData })
    } else {
      createMutation.mutate(formData)
    }
  }

  const isSaving = createMutation.isPending || updateMutation.isPending
  const mutationError = createMutation.error ?? updateMutation.error

  const studentOptions = [
    { value: '', label: 'All Students' },
    ...(studentsData?.content ?? []).map((s) => ({
      value: s.id,
      label: `${s.fullName} (${s.studentNumber})`,
    })),
  ]

  const studentSelectOptions = (studentsData?.content ?? []).map((s) => ({
    value: s.id,
    label: `${s.fullName} (${s.studentNumber})`,
  }))

  const semesterOptions = SEMESTERS.map((s) => ({ value: s, label: s }))

  const ScoreCell = ({ score }: { score: number }) => (
    <span className={`text-sm font-medium ${getScoreColor(score)}`}>{score.toFixed(1)}</span>
  )

  const columns: Column<Grade>[] = [
    {
      key: 'studentName',
      header: 'Student',
      sortable: true,
      render: (row) => (
        <span className="font-medium text-gray-900 text-sm">{row.studentName}</span>
      ),
    },
    {
      key: 'courseName',
      header: 'Course',
      sortable: true,
      render: (row) => <span className="text-sm">{row.courseName}</span>,
    },
    {
      key: 'semester',
      header: 'Semester',
      sortable: true,
      render: (row) => (
        <span className="text-xs bg-blue-50 text-blue-700 px-2 py-0.5 rounded-full font-medium">
          {row.semester}
        </span>
      ),
    },
    {
      key: 'assignmentScore',
      header: 'Assignment',
      sortable: true,
      render: (row) => <ScoreCell score={row.assignmentScore} />,
    },
    {
      key: 'midtermScore',
      header: 'Midterm',
      sortable: true,
      render: (row) => <ScoreCell score={row.midtermScore} />,
    },
    {
      key: 'finalScore',
      header: 'Final',
      sortable: true,
      render: (row) => <ScoreCell score={row.finalScore} />,
    },
    {
      key: 'totalScore',
      header: 'Total',
      sortable: true,
      render: (row) => (
        <span className={`text-sm font-bold ${getScoreColor(row.totalScore)}`}>
          {row.totalScore.toFixed(1)}
        </span>
      ),
    },
    {
      key: 'letterGrade',
      header: 'Grade',
      sortable: true,
      render: (row) => <GradeBadge grade={row.letterGrade} />,
    },
    {
      key: 'createdAt',
      header: 'Date',
      render: (row) => (
        <span className="text-xs text-gray-400">{formatDate(row.createdAt)}</span>
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
              setDeletingGrade(row)
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
          <h1 className="text-2xl font-bold text-gray-900">Grades</h1>
          <p className="text-gray-500 text-sm mt-0.5">
            Manage grade records ({data?.totalElements ?? 0} total)
          </p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4" />
          Add Grade
        </Button>
      </div>

      <Card>
        <CardHeader className="pb-4">
          <div className="flex items-center gap-3 flex-wrap">
            <div className="flex items-center gap-2">
              <Filter className="h-4 w-4 text-gray-400" />
              <label className="text-sm text-gray-500 font-medium">Filter by student:</label>
            </div>
            <select
              value={filterStudentId ?? ''}
              onChange={(e) => {
                setFilterStudentId(e.target.value ? Number(e.target.value) : null)
                setPage(0)
              }}
              className="h-9 px-3 rounded-md border border-gray-300 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 min-w-[200px]"
            >
              {studentOptions.map((opt) => (
                <option key={String(opt.value)} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </select>
            {filterStudentId && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => {
                  setFilterStudentId(null)
                  setPage(0)
                }}
              >
                Clear Filter
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
              <p>Failed to load grades.</p>
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
                emptyMessage="No grades found. Add one to get started."
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
        title={editingGrade ? 'Edit Grade' : 'Add New Grade'}
        description={
          editingGrade
            ? `Editing grade for ${editingGrade.studentName}`
            : 'Fill in the details to record a new grade.'
        }
        size="lg"
        footer={
          <div className="flex justify-end gap-3">
            <Button variant="outline" onClick={closeForm} disabled={isSaving}>
              Cancel
            </Button>
            <Button onClick={handleSubmit(onSubmit)} loading={isSaving}>
              {editingGrade ? 'Save Changes' : 'Add Grade'}
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

          <Select
            label="Student"
            options={studentSelectOptions}
            placeholder="Select a student"
            disabled={!!editingGrade}
            {...register('studentId')}
            error={errors.studentId?.message}
            helperText={editingGrade ? 'Student cannot be changed.' : undefined}
          />

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Course Name"
              placeholder="Introduction to CS"
              {...register('courseName')}
              error={errors.courseName?.message}
            />
            <Select
              label="Semester"
              options={semesterOptions}
              placeholder="Select semester"
              {...register('semester')}
              error={errors.semester?.message}
            />
          </div>

          <div className="border-t border-gray-100 pt-4">
            <p className="text-sm font-medium text-gray-700 mb-3">
              Scores <span className="text-gray-400 font-normal">(0 – 100)</span>
            </p>
            <div className="grid grid-cols-3 gap-4">
              <Input
                label="Assignment"
                type="number"
                min={0}
                max={100}
                step={0.1}
                placeholder="0"
                {...register('assignmentScore')}
                error={errors.assignmentScore?.message}
              />
              <Input
                label="Midterm"
                type="number"
                min={0}
                max={100}
                step={0.1}
                placeholder="0"
                {...register('midtermScore')}
                error={errors.midtermScore?.message}
              />
              <Input
                label="Final"
                type="number"
                min={0}
                max={100}
                step={0.1}
                placeholder="0"
                {...register('finalScore')}
                error={errors.finalScore?.message}
              />
            </div>
            <p className="mt-2 text-xs text-gray-400">
              Total score and letter grade are automatically calculated by the server.
            </p>
          </div>
        </form>
      </Modal>

      {/* Delete confirmation */}
      <ConfirmDialog
        isOpen={!!deletingGrade}
        onClose={() => setDeletingGrade(null)}
        onConfirm={() => deletingGrade && deleteMutation.mutate(deletingGrade.id)}
        title="Delete Grade"
        description={`Are you sure you want to delete the grade for ${deletingGrade?.studentName} in ${deletingGrade?.courseName}? This action cannot be undone.`}
        confirmLabel="Delete"
        isLoading={deleteMutation.isPending}
        variant="danger"
      />
    </div>
  )
}
