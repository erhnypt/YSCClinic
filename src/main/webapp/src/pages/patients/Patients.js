import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Paper,
  Button,
  Typography,
  TextField,
  InputAdornment,
  IconButton
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Clear as ClearIcon
} from '@mui/icons-material';
import { DataGrid } from '@mui/x-data-grid';
import axios from 'axios';

function Patients() {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const navigate = useNavigate();

  const columns = [
    { field: 'tcNo', headerName: 'TC No', width: 150 },
    { field: 'firstName', headerName: 'Ad', width: 150 },
    { field: 'lastName', headerName: 'Soyad', width: 150 },
    {
      field: 'dateOfBirth',
      headerName: 'Doğum Tarihi',
      width: 150,
      valueFormatter: (params) => {
        if (!params.value) return '';
        return new Date(params.value).toLocaleDateString('tr-TR');
      }
    },
    { field: 'gender', headerName: 'Cinsiyet', width: 120 },
    { field: 'phoneNumber', headerName: 'Telefon', width: 150 },
    { field: 'email', headerName: 'E-posta', width: 200 },
    {
      field: 'actions',
      headerName: 'İşlemler',
      width: 120,
      sortable: false,
      renderCell: (params) => (
        <Button
          variant="contained"
          size="small"
          onClick={() => navigate(`/patients/${params.row.id}`)}
        >
          Düzenle
        </Button>
      ),
    },
  ];

  const fetchPatients = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/api/patients', {
        params: {
          page,
          size: pageSize,
          search: searchTerm
        }
      });
      setPatients(response.data.data.content);
      setTotalElements(response.data.data.totalElements);
    } catch (error) {
      console.error('Hastalar yüklenirken hata oluştu:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPatients();
  }, [page, pageSize, searchTerm]);

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
    setPage(0);
  };

  const clearSearch = () => {
    setSearchTerm('');
    setPage(0);
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h5" component="h2">
          Hastalar
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/patients/new')}
        >
          Yeni Hasta
        </Button>
      </Box>

      <Paper sx={{ p: 2, mb: 3 }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Hasta ara..."
          value={searchTerm}
          onChange={handleSearch}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
            endAdornment: searchTerm && (
              <InputAdornment position="end">
                <IconButton onClick={clearSearch} size="small">
                  <ClearIcon />
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
      </Paper>

      <Paper sx={{ height: 600, width: '100%' }}>
        <DataGrid
          rows={patients}
          columns={columns}
          pagination
          paginationMode="server"
          rowCount={totalElements}
          page={page}
          pageSize={pageSize}
          onPageChange={(newPage) => setPage(newPage)}
          onPageSizeChange={(newPageSize) => setPageSize(newPageSize)}
          loading={loading}
          disableSelectionOnClick
          sx={{
            '& .MuiDataGrid-cell:focus': {
              outline: 'none',
            },
          }}
        />
      </Paper>
    </Box>
  );
}

export default Patients; 