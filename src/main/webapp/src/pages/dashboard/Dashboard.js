import React, { useState, useEffect, useMemo, useCallback } from 'react';
import {
  Box,
  Grid,
  Paper,
  Typography,
  CircularProgress,
  Card,
  CardContent,
  IconButton,
  Tooltip,
  useTheme
} from '@mui/material';
import {
  People as PeopleIcon,
  Today as TodayIcon,
  CalendarMonth as CalendarMonthIcon,
  Refresh as RefreshIcon,
  Autorenew as AutorenewIcon
} from '@mui/icons-material';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  Legend,
  ResponsiveContainer,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell
} from 'recharts';
import axios from 'axios';

const ChartContainer = React.memo(({ title, loading, error, children }) => (
  <Paper sx={{ p: 3 }}>
    <Typography variant="h6" sx={{ mb: 2 }}>
      {title}
    </Typography>
    {loading ? (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
        <CircularProgress />
      </Box>
    ) : error ? (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
        <Typography color="error">
          Veriler yüklenirken bir hata oluştu
        </Typography>
      </Box>
    ) : (
      children
    )}
  </Paper>
));

const StatCard = React.memo(({ title, value, icon, color }) => (
  <Card sx={{ height: '100%' }}>
    <CardContent>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <Box sx={{ 
          backgroundColor: `${color}20`,
          borderRadius: '50%',
          p: 1,
          mr: 2,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}>
          {React.cloneElement(icon, { sx: { color: color, fontSize: 32 } })}
        </Box>
        <Typography variant="h6" component="div" color="text.secondary">
          {title}
        </Typography>
      </Box>
      <Typography variant="h4" component="div">
        {value.toLocaleString('tr-TR')}
      </Typography>
    </CardContent>
  </Card>
));

function Dashboard() {
  const theme = useTheme();
  const [stats, setStats] = useState({
    totalPatients: 0,
    todayAppointments: 0,
    monthlyAppointments: 0,
    patientGrowth: [],
    appointmentsByDay: [],
    genderDistribution: [],
    ageDistribution: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);
  const AUTO_REFRESH_INTERVAL = 5 * 60 * 1000; // 5 dakika

  const validateData = useCallback((data) => {
    if (!data) return false;
    
    try {
      // Temel istatistiklerin kontrolü
      if (typeof data.totalPatients !== 'number' ||
          typeof data.todayAppointments !== 'number' ||
          typeof data.monthlyAppointments !== 'number') {
        return false;
      }

      // Array kontrolü ve minimum veri gereksinimleri
      if (!Array.isArray(data.patientGrowth) ||
          !Array.isArray(data.appointmentsByDay) ||
          !Array.isArray(data.genderDistribution) ||
          !Array.isArray(data.ageDistribution)) {
        return false;
      }

      // Array içeriklerinin kontrolü
      return data.patientGrowth.every(item => item.date && typeof item.count === 'number') &&
             data.appointmentsByDay.every(item => item.day && typeof item.count === 'number') &&
             data.genderDistribution.every(item => item.name && typeof item.value === 'number') &&
             data.ageDistribution.every(item => item.range && typeof item.count === 'number');
    } catch (e) {
      console.error('Veri doğrulama hatası:', e);
      return false;
    }
  }, []);

  const fetchStats = useCallback(async () => {
    try {
      setLoading(true);
      const response = await axios.get('/api/dashboard/stats', {
        headers: {
          'Cache-Control': 'no-cache',
          'Pragma': 'no-cache'
        }
      });
      const data = response.data.data;

      if (!validateData(data)) {
        throw new Error('Geçersiz veri formatı');
      }

      setStats(data);
      setLastUpdated(new Date());
      setError('');
    } catch (err) {
      let errorMessage = 'İstatistikler yüklenirken bir hata oluştu';
      
      if (err.response) {
        errorMessage = err.response.data.message || errorMessage;
      } else if (err.message === 'Geçersiz veri formatı') {
        errorMessage = 'Sunucudan gelen veriler geçersiz formatta';
      } else if (err.request) {
        errorMessage = 'Sunucuya ulaşılamıyor';
      }

      setError(errorMessage);
      console.error('İstatistikler yüklenirken hata:', err);
    } finally {
      setLoading(false);
    }
  }, [validateData]);

  useEffect(() => {
    fetchStats();

    let intervalId;
    if (autoRefresh) {
      intervalId = setInterval(fetchStats, AUTO_REFRESH_INTERVAL);
    }

    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [autoRefresh, fetchStats]);

  const chartData = useMemo(() => ({
    patientGrowth: stats.patientGrowth.map(item => ({
      ...item,
      date: new Date(item.date).toLocaleDateString('tr-TR')
    })),
    appointmentsByDay: stats.appointmentsByDay,
    genderDistribution: stats.genderDistribution,
    ageDistribution: stats.ageDistribution
  }), [stats]);

  const toggleAutoRefresh = useCallback(() => {
    setAutoRefresh(prev => !prev);
  }, []);

  const COLORS = useMemo(() => ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'], []);

  if (loading && !stats.totalPatients) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" component="h2">
            Dashboard
          </Typography>
          {lastUpdated && (
            <Typography variant="caption" color="text.secondary">
              Son güncelleme: {lastUpdated.toLocaleTimeString('tr-TR')}
            </Typography>
          )}
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Tooltip title={autoRefresh ? "Otomatik yenileme açık" : "Otomatik yenileme kapalı"}>
            <IconButton 
              onClick={toggleAutoRefresh} 
              color={autoRefresh ? "primary" : "default"}
              size="large"
            >
              <AutorenewIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title="Yenile">
            <IconButton 
              onClick={() => fetchStats()} 
              size="large"
              disabled={loading}
            >
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {error && (
        <Paper sx={{ p: 2, mb: 3, bgcolor: 'error.light', color: 'error.contrastText' }}>
          <Typography>{error}</Typography>
        </Paper>
      )}

      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <StatCard
            title="Toplam Hasta"
            value={stats.totalPatients}
            icon={<PeopleIcon />}
            color="#1976d2"
          />
        </Grid>
        <Grid item xs={12} md={4}>
          <StatCard
            title="Bugünkü Randevular"
            value={stats.todayAppointments}
            icon={<TodayIcon />}
            color="#2e7d32"
          />
        </Grid>
        <Grid item xs={12} md={4}>
          <StatCard
            title="Aylık Randevular"
            value={stats.monthlyAppointments}
            icon={<CalendarMonthIcon />}
            color="#ed6c02"
          />
        </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mt: 1 }}>
        {/* Hasta Artış Grafiği */}
        <Grid item xs={12} lg={6}>
          <ChartContainer title="Hasta Artış Grafiği" loading={loading} error={error}>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={chartData.patientGrowth}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <RechartsTooltip />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="count"
                  stroke={theme.palette.primary.main}
                  name="Hasta Sayısı"
                />
              </LineChart>
            </ResponsiveContainer>
          </ChartContainer>
        </Grid>

        {/* Haftalık Randevu Dağılımı */}
        <Grid item xs={12} lg={6}>
          <ChartContainer title="Haftalık Randevu Dağılımı" loading={loading} error={error}>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={chartData.appointmentsByDay}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="day" />
                <YAxis />
                <RechartsTooltip />
                <Legend />
                <Bar
                  dataKey="count"
                  fill={theme.palette.secondary.main}
                  name="Randevu Sayısı"
                />
              </BarChart>
            </ResponsiveContainer>
          </ChartContainer>
        </Grid>

        {/* Cinsiyet Dağılımı */}
        <Grid item xs={12} md={6}>
          <ChartContainer title="Cinsiyet Dağılımı" loading={loading} error={error}>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={chartData.genderDistribution}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} (${(percent * 100).toFixed(0)}%)`}
                  outerRadius={100}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {chartData.genderDistribution.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <RechartsTooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </ChartContainer>
        </Grid>

        {/* Yaş Dağılımı */}
        <Grid item xs={12} md={6}>
          <ChartContainer title="Yaş Dağılımı" loading={loading} error={error}>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={chartData.ageDistribution}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="range" />
                <YAxis />
                <RechartsTooltip />
                <Legend />
                <Bar
                  dataKey="count"
                  fill={theme.palette.info.main}
                  name="Hasta Sayısı"
                />
              </BarChart>
            </ResponsiveContainer>
          </ChartContainer>
        </Grid>
      </Grid>
    </Box>
  );
}

export default React.memo(Dashboard); 