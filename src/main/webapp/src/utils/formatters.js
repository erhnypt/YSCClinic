export const formatDate = (date) => {
  if (!date) return '';
  
  try {
    return new Date(date).toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  } catch (e) {
    console.error('Tarih formatlanırken hata:', e);
    return '';
  }
};

export const formatNumber = (number) => {
  if (typeof number !== 'number') return '0';
  
  try {
    return number.toLocaleString('tr-TR');
  } catch (e) {
    console.error('Sayı formatlanırken hata:', e);
    return '0';
  }
};

export const formatTime = (date) => {
  if (!date) return '';
  
  try {
    return new Date(date).toLocaleTimeString('tr-TR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    console.error('Saat formatlanırken hata:', e);
    return '';
  }
};

export const formatShortDate = (date) => {
  if (!date) return '';
  
  try {
    return new Date(date).toLocaleDateString('tr-TR', {
      month: 'short',
      day: 'numeric'
    });
  } catch (e) {
    console.error('Kısa tarih formatlanırken hata:', e);
    return '';
  }
}; 