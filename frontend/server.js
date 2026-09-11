const express = require('express');
const axios = require('axios');
const path = require('path');

const app = express();
const PORT = 3000;
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8081';

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.use(express.urlencoded({ extended: true }));

// チケット一覧表示画面
app.get('/', async (req, res) => {
  try {
    // Spring Boot の API 呼出
    const response = await axios.get(`${BACKEND_URL}/api/tickets`);
    res.render('index', { tickets: response.data, error: null });
  } catch (error) {
    console.error('バックエンド通信エラー:', error.message);
    res.render('index', { tickets: [], error: 'バックエンドからのデータ取得に失敗しました。' });
  }
});

app.listen(PORT, () => {
  console.log(`Frontend server running on port ${PORT}`);
});
