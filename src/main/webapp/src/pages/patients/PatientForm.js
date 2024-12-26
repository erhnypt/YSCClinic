import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Grid,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  Alert,
  CircularProgress
} from '@mui/material';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { useFormik } from 'formik';
import * as yup from 'yup';
import axios from 'axios';
import trLocale from 'date-fns/locale/tr';

const validationSchema = yup.object({
  tcNo: yup
    .string()
    .required('TC Kimlik numarası zorunludur')
    .matches(/^[0-9]{11}$/, 'TC Kimlik numarası 11 haneli olmalıdır'),
  firstName: yup
    .string()
    .required('Ad zorunludur')
    .min(2, 'Ad en az 2 karakter olmalıdır'),
  lastName: yup
    .string()
    .required('Soyad zorunludur')
    .min(2, 'Soyad en az 2 karakter olmalıdır'),
  dateOfBirth: yup
    .date()
    .required('Doğum tarihi zorunludur')
    .max(new Date(), 'Doğum tarihi bugünden büyük olamaz'),
  gender: yup
    .string()
    .required('Cinsiyet zorunludur'),
  phoneNumber: yup
    .string()
    .required('Telefon numarası zorunludur')
    .matches(/^[0-9]{10}$/, 'Telefon numarası 10 haneli olmalıdır'),
  email: yup
    .string()
    .email('Geçerli bir e-posta adresi giriniz'),
  address: yup
    .string()
    .required('Adres zorunludur')
});

function PatientForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const formik = useFormik({
    initialValues: {
      tcNo: '',
      firstName: '',
      lastName: '',
      dateOfBirth: null,
      gender: '',
      phoneNumber: '',
      email: '',
      address: ''
    },
    validationSchema: validationSchema,
    onSubmit: async (values) => {
      try {
        setLoading(true);
        setError('');
        setSuccess('');

        if (id) {
          await axios.put(`/api/patients/${id}`, values);
          setSuccess('Hasta başarıyla güncellendi');
        } else {
          await axios.post('/api/patients', values);
          setSuccess('Hasta başarıyla kaydedildi');
        }

        setTimeout(() => {
          navigate('/patients');
        }, 1500);
      } catch (err) {
        setError(err.response?.data?.message || 'Bir hata oluştu');
      } finally {
        setLoading(false);
      }
    },
  });

  useEffect(() => {
    const fetchPatient = async () => {
      if (!id) return;

      try {
        setLoading(true);
        const response = await axios.get(`/api/patients/${id}`);
        const patient = response.data.data;
        formik.setValues({
          tcNo: patient.tcNo,
          firstName: patient.firstName,
          lastName: patient.lastName,
          dateOfBirth: new Date(patient.dateOfBirth),
          gender: patient.gender,
          phoneNumber: patient.phoneNumber,
          email: patient.email || '',
          address: patient.address
        });
      } catch (err) {
        setError('Hasta bilgileri yüklenirken hata oluştu');
      } finally {
        setLoading(false);
      }
    };

    fetchPatient();
  }, [id]);

  if (loading && !formik.dirty) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h5" component="h2" sx={{ mb: 3 }}>
        {id ? 'Hasta Düzenle' : 'Yeni Hasta'}
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          {success}
        </Alert>
      )}

      <Paper sx={{ p: 3 }}>
        <form onSubmit={formik.handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                id="tcNo"
                name="tcNo"
                label="TC Kimlik No"
                value={formik.values.tcNo}
                onChange={formik.handleChange}
                error={formik.touched.tcNo && Boolean(formik.errors.tcNo)}
                helperText={formik.touched.tcNo && formik.errors.tcNo}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                id="firstName"
                name="firstName"
                label="Ad"
                value={formik.values.firstName}
                onChange={formik.handleChange}
                error={formik.touched.firstName && Boolean(formik.errors.firstName)}
                helperText={formik.touched.firstName && formik.errors.firstName}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                id="lastName"
                name="lastName"
                label="Soyad"
                value={formik.values.lastName}
                onChange={formik.handleChange}
                error={formik.touched.lastName && Boolean(formik.errors.lastName)}
                helperText={formik.touched.lastName && formik.errors.lastName}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={trLocale}>
                <DatePicker
                  label="Doğum Tarihi"
                  value={formik.values.dateOfBirth}
                  onChange={(value) => formik.setFieldValue('dateOfBirth', value)}
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      fullWidth
                      error={formik.touched.dateOfBirth && Boolean(formik.errors.dateOfBirth)}
                      helperText={formik.touched.dateOfBirth && formik.errors.dateOfBirth}
                    />
                  )}
                />
              </LocalizationProvider>
            </Grid>

            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel id="gender-label">Cinsiyet</InputLabel>
                <Select
                  labelId="gender-label"
                  id="gender"
                  name="gender"
                  value={formik.values.gender}
                  onChange={formik.handleChange}
                  error={formik.touched.gender && Boolean(formik.errors.gender)}
                  label="Cinsiyet"
                >
                  <MenuItem value="ERKEK">Erkek</MenuItem>
                  <MenuItem value="KADIN">Kadın</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                id="phoneNumber"
                name="phoneNumber"
                label="Telefon Numarası"
                value={formik.values.phoneNumber}
                onChange={formik.handleChange}
                error={formik.touched.phoneNumber && Boolean(formik.errors.phoneNumber)}
                helperText={formik.touched.phoneNumber && formik.errors.phoneNumber}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                id="email"
                name="email"
                label="E-posta"
                value={formik.values.email}
                onChange={formik.handleChange}
                error={formik.touched.email && Boolean(formik.errors.email)}
                helperText={formik.touched.email && formik.errors.email}
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                id="address"
                name="address"
                label="Adres"
                multiline
                rows={4}
                value={formik.values.address}
                onChange={formik.handleChange}
                error={formik.touched.address && Boolean(formik.errors.address)}
                helperText={formik.touched.address && formik.errors.address}
              />
            </Grid>

            <Grid item xs={12}>
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                <Button
                  variant="outlined"
                  onClick={() => navigate('/patients')}
                  disabled={loading}
                >
                  İptal
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  disabled={loading}
                >
                  {loading ? <CircularProgress size={24} /> : (id ? 'Güncelle' : 'Kaydet')}
                </Button>
              </Box>
            </Grid>
          </Grid>
        </form>
      </Paper>
    </Box>
  );
}

export default PatientForm; 