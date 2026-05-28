export interface User {
  id: number
  username: string
  fullName: string
  email: string
  role: 'ADMIN' | 'TEACHER' | 'STUDENT'
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  userId: number
  username: string
  fullName: string
  role: string
}

export interface Student {
  id: number
  studentNumber: string
  firstName: string
  lastName: string
  fullName: string
  email: string
  phone: string
  address: string
  createdAt: string
}

export interface Teacher {
  id: number
  employeeId: string
  fullName: string
  department: string
  phone: string
  createdAt: string
}

export interface Grade {
  id: number
  studentId: number
  studentName: string
  courseName: string
  semester: string
  assignmentScore: number
  midtermScore: number
  finalScore: number
  totalScore: number
  letterGrade: string
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

export interface GradeStats {
  averageScore: number
  gradeDistribution: Record<string, number>
}

export interface CourseAverage {
  courseName: string
  average: number
}

export interface DashboardStats {
  totalStudents: number
  totalTeachers: number
  totalGrades: number
  averageScore: number
}
