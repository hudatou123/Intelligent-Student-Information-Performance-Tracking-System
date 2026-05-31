import { useQuery } from '@tanstack/react-query'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'
import { Users, GraduationCap, BookOpen, TrendingUp, AlertCircle } from 'lucide-react'
import { gradeApi } from '@/api/grades'
import { studentApi } from '@/api/students'
import { teacherApi } from '@/api/teachers'
import { useAuthStore } from '@/store/authStore'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card'
import { GradeBadge } from '@/components/ui/Badge'
import { formatDate, getScoreColor } from '@/lib/utils'

interface StatCardProps {
  title: string
  value: string | number
  icon: React.ElementType
  color: string
  subtitle?: string
}

function StatCard({ title, value, icon: Icon, color, subtitle }: StatCardProps) {
  return (
    <Card>
      <CardContent className="p-6">
        <div className="flex items-start justify-between">
          <div>
            <p className="text-sm font-medium text-gray-500">{title}</p>
            <p className="mt-2 text-3xl font-bold text-gray-900">{value}</p>
            {subtitle && (
              <p className="mt-1 text-xs text-gray-400">{subtitle}</p>
            )}
          </div>
          <div className={`p-3 rounded-xl ${color}`}>
            <Icon className="h-6 w-6" />
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

export default function Dashboard() {
  const { user } = useAuthStore()

  const { data: studentsData } = useQuery({
    queryKey: ['students', 'summary'],
    queryFn: () => studentApi.getAll(0, 1),
  })

  const { data: teachersData } = useQuery({
    queryKey: ['teachers', 'summary'],
    queryFn: () => teacherApi.getAll(0, 1),
  })

  const { data: gradesData } = useQuery({
    queryKey: ['grades', 'summary'],
    queryFn: () => gradeApi.getAll(0, 1),
  })

  const { data: courseAverages, isLoading: isLoadingChart } = useQuery({
    queryKey: ['grades', 'courseAverages'],
    queryFn: gradeApi.getCourseAverages,
  })

  const { data: recentGrades, isLoading: isLoadingRecent } = useQuery({
    queryKey: ['grades', 'recent'],
    queryFn: () => gradeApi.getAll(0, 5),
  })

  const totalStudents = studentsData?.totalElements ?? 0
  const totalTeachers = teachersData?.totalElements ?? 0
  const totalGrades = gradesData?.totalElements ?? 0

  const avgScore =
    recentGrades?.content && recentGrades.content.length > 0
      ? Math.round(
          recentGrades.content.reduce((sum, g) => sum + g.totalScore, 0) /
            recentGrades.content.length
        )
      : 0

  const chartData =
    courseAverages?.map((c) => ({
      name: c.courseName.length > 15 ? c.courseName.substring(0, 15) + '…' : c.courseName,
      fullName: c.courseName,
      average: Math.round(c.average * 10) / 10,
    })) ?? []

  return (
    <div className="space-y-6">
      {/* Welcome header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          Welcome back, {user?.fullName ?? 'User'}!
        </h1>
        <p className="text-gray-500 mt-1 text-sm">
          Here's an overview of the student tracking system.
        </p>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        <StatCard
          title="Total Students"
          value={totalStudents}
          icon={GraduationCap}
          color="bg-blue-100 text-blue-600"
        />
        <StatCard
          title="Total Teachers"
          value={totalTeachers}
          icon={Users}
          color="bg-purple-100 text-purple-600"
        />
        <StatCard
          title="Total Grades"
          value={totalGrades}
          icon={BookOpen}
          color="bg-green-100 text-green-600"
        />
        <StatCard
          title="Avg Score (Recent)"
          value={avgScore > 0 ? `${avgScore}` : '—'}
          icon={TrendingUp}
          color="bg-orange-100 text-orange-600"
          subtitle="Based on recent grades"
        />
      </div>

      {/* Chart + Recent grades */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        {/* Course averages chart */}
        <Card>
          <CardHeader>
            <CardTitle>Course Score Averages</CardTitle>
          </CardHeader>
          <CardContent>
            {isLoadingChart ? (
              <div className="h-64 flex items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent" />
              </div>
            ) : chartData.length === 0 ? (
              <div className="h-64 flex flex-col items-center justify-center text-gray-400 gap-2">
                <AlertCircle className="h-8 w-8" />
                <p className="text-sm">No course data available</p>
              </div>
            ) : (
              <ResponsiveContainer width="100%" height={280}>
                <BarChart
                  data={chartData}
                  margin={{ top: 5, right: 10, left: -10, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                  <XAxis
                    dataKey="name"
                    tick={{ fontSize: 11, fill: '#6b7280' }}
                    tickLine={false}
                  />
                  <YAxis
                    domain={[0, 100]}
                    tick={{ fontSize: 11, fill: '#6b7280' }}
                    tickLine={false}
                    axisLine={false}
                  />
                  <Tooltip
                    formatter={(value: number) => [`${value}`, 'Average Score']}
                    labelFormatter={(_, payload) =>
                      payload?.[0]?.payload?.fullName ?? ''
                    }
                    contentStyle={{
                      borderRadius: '8px',
                      border: '1px solid #e5e7eb',
                      boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)',
                    }}
                  />
                  <Bar
                    dataKey="average"
                    fill="#2563eb"
                    radius={[4, 4, 0, 0]}
                    maxBarSize={60}
                  />
                </BarChart>
              </ResponsiveContainer>
            )}
          </CardContent>
        </Card>

        {/* Recent grades */}
        <Card>
          <CardHeader>
            <CardTitle>Recent Grades</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {isLoadingRecent ? (
              <div className="p-6 space-y-3">
                {[...Array(5)].map((_, i) => (
                  <div key={i} className="h-10 bg-gray-100 rounded animate-pulse" />
                ))}
              </div>
            ) : !recentGrades?.content?.length ? (
              <div className="p-6 text-center text-gray-400 text-sm">
                No grades recorded yet.
              </div>
            ) : (
              <div className="divide-y divide-gray-100">
                {recentGrades.content.map((grade) => (
                  <div
                    key={grade.id}
                    className="flex items-center justify-between px-6 py-3.5 hover:bg-gray-50 transition-colors"
                  >
                    <div className="min-w-0 flex-1">
                      <p className="text-sm font-medium text-gray-900 truncate">
                        {grade.studentName}
                      </p>
                      <p className="text-xs text-gray-400 truncate">
                        {grade.courseName} &middot; {grade.semester}
                      </p>
                    </div>
                    <div className="flex items-center gap-3 ml-4 flex-shrink-0">
                      <span
                        className={`text-sm font-semibold ${getScoreColor(grade.totalScore)}`}
                      >
                        {grade.totalScore.toFixed(1)}
                      </span>
                      <GradeBadge grade={grade.letterGrade} />
                    </div>
                  </div>
                ))}
              </div>
            )}

            {recentGrades?.content && recentGrades.content.length > 0 && (
              <div className="px-6 py-3 border-t border-gray-100">
                <p className="text-xs text-gray-400 text-right">
                  Showing {recentGrades.content.length} of {recentGrades.totalElements} total grades
                </p>
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      {/* Grade distribution summary */}
      {recentGrades?.content && recentGrades.content.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>Recent Grade Distribution</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex flex-wrap gap-3">
              {(['A', 'B', 'C', 'D', 'F'] as const).map((letter) => {
                const count = recentGrades.content.filter(
                  (g) => g.letterGrade === letter
                ).length
                return (
                  <div
                    key={letter}
                    className="flex items-center gap-2 px-4 py-2.5 rounded-lg bg-gray-50 border border-gray-200"
                  >
                    <GradeBadge grade={letter} />
                    <span className="text-sm font-semibold text-gray-700">{count}</span>
                    <span className="text-xs text-gray-400">
                      ({recentGrades.content.length > 0
                        ? Math.round((count / recentGrades.content.length) * 100)
                        : 0}%)
                    </span>
                  </div>
                )
              })}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
