import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider } from './contexts/AuthContext';
import Layout from './components/layout/Layout';
import Login from './pages/auth/Login';
import Dashboard from './pages/dashboard/Dashboard';
import Patients from './pages/patients/Patients';
import PatientForm from './pages/patients/PatientForm';
import Users from './pages/users/Users';
import UserForm from './pages/users/UserForm';
import ProtectedRoute from './components/auth/ProtectedRoute';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    fontFamily: [
      'Roboto',
      'Arial',
      'sans-serif'
    ].join(','),
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
              <Route index element={<Navigate to="/dashboard" replace />} />
              <Route path="dashboard" element={<Dashboard />} />
              <Route path="patients" element={<Patients />} />
              <Route path="patients/new" element={<PatientForm />} />
              <Route path="patients/:id" element={<PatientForm />} />
              <Route path="users" element={<Users />} />
              <Route path="users/new" element={<UserForm />} />
              <Route path="users/:id" element={<UserForm />} />
            </Route>
          </Routes>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App; 