import api from '@/lib/axios'
import type { Grade, PageResponse, ApiResponse, GradeStats, CourseAverage } from '@/types'

export interface GradeCreatePayload {
  studentId: number
  courseName: string
  semester: string
  assignmentScore: number
  midtermScore: number
  finalScore: number
}

export interface GradeUpdatePayload {
  courseName?: string
  semester?: string
  assignmentScore?: number
  midtermScore?: number
  finalScore?: number
}

export const gradeApi = {
  getAll: async (page = 0, size = 10): Promise<PageResponse<Grade>> => {
    const { data } = await api.get<ApiResponse<PageResponse<Grade>>>('/grades', {
      params: { page, size },
    })
    return data.data
  },

  getByStudent: async (studentId: number, page = 0, size = 10): Promise<PageResponse<Grade>> => {
    const { data } = await api.get<ApiResponse<PageResponse<Grade>>>(
      `/grades/student/${studentId}`,
      { params: { page, size } }
    )
    return data.data
  },

  getById: async (id: number): Promise<Grade> => {
    const { data } = await api.get<ApiResponse<Grade>>(`/grades/${id}`)
    return data.data
  },

  create: async (payload: GradeCreatePayload): Promise<Grade> => {
    const { data } = await api.post<ApiResponse<Grade>>('/grades', payload)
    return data.data
  },

  update: async (id: number, payload: GradeUpdatePayload): Promise<Grade> => {
    const { data } = await api.put<ApiResponse<Grade>>(`/grades/${id}`, payload)
    return data.data
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/grades/${id}`)
  },

  getStudentStats: async (studentId: number): Promise<GradeStats> => {
    const { data } = await api.get<ApiResponse<GradeStats>>(`/grades/stats/student/${studentId}`)
    return data.data
  },

  getCourseAverages: async (): Promise<CourseAverage[]> => {
    const { data } = await api.get<ApiResponse<CourseAverage[]>>('/grades/stats/courses')
    return data.data
  },

  getDashboardStats: async () => {
    const { data } = await api.get<ApiResponse<{
      totalStudents: number
      totalTeachers: number
      totalGrades: number
      averageScore: number
    }>>('/grades/stats/dashboard')
    return data.data
  },

  getRecent: async (limit = 5): Promise<Grade[]> => {
    const { data } = await api.get<ApiResponse<Grade[]>>('/grades/recent', {
      params: { limit },
    })
    return data.data
  },
}
