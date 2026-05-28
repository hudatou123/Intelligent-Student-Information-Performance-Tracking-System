import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate, useLocation } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { Award, AlertCircle } from 'lucide-react'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/authStore'
import { Button } from '@/components/ui/Button'
import { Input } from '@/components/ui/Input'

const loginSchema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
})

type LoginFormData = z.infer<typeof loginSchema>

export default function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const { setAuth } = useAuthStore()
  const from = (location.state as { from?: { pathname: string } })?.from?.pathname ?? '/dashboard'

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  const { mutate, isPending, error } = useMutation({
    mutationFn: ({ username, password }: LoginFormData) =>
      authApi.login(username, password),
    onSuccess: (data) => {
      setAuth(data.accessToken, data.refreshToken, {
        id: data.userId,
        username: data.username,
        fullName: data.fullName,
        role: data.role,
      })
      navigate(from, { replace: true })
    },
  })

  const onSubmit = (data: LoginFormData) => {
    mutate(data)
  }

  const errorMessage =
    error instanceof Error
      ? (error as { response?: { data?: { message?: string } } }).response?.data?.message ??
        error.message
      : null

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-14 h-14 bg-blue-600 rounded-2xl mb-4 shadow-lg">
            <Award className="h-8 w-8 text-white" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900">Grade Management</h1>
          <p className="text-gray-500 mt-1 text-sm">Sign in to your account</p>
        </div>

        {/* Card */}
        <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            {/* Error banner */}
            {errorMessage && (
              <div className="flex items-start gap-2.5 p-3.5 rounded-lg bg-red-50 border border-red-200 text-red-700">
                <AlertCircle className="h-4 w-4 mt-0.5 flex-shrink-0" />
                <p className="text-sm">{errorMessage}</p>
              </div>
            )}

            <Input
              label="Username"
              placeholder="Enter your username"
              autoComplete="username"
              autoFocus
              {...register('username')}
              error={errors.username?.message}
            />

            <div>
              <Input
                label="Password"
                type="password"
                placeholder="Enter your password"
                autoComplete="current-password"
                {...register('password')}
                error={errors.password?.message}
              />
            </div>

            <Button
              type="submit"
              className="w-full"
              size="lg"
              loading={isPending}
            >
              {isPending ? 'Signing in…' : 'Sign In'}
            </Button>
          </form>

          <div className="mt-6 pt-5 border-t border-gray-100 text-center">
            <p className="text-xs text-gray-400">
              Grade Management System &copy; {new Date().getFullYear()}
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
