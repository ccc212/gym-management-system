import moment from 'moment';

/**
 * 格式化日期时间为后端需要的字符串格式
 * @param date 日期对象或字符串
 * @param format 格式，默认 'YYYY-MM-DD HH:mm:ss'
 */
export function formatDateTime(date: any, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return '';
  return moment(date).format(format);
}

/**
 * 转换为 ISO 字符串（如后端需要 UTC 格式时可用）
 * @param date 日期对象或字符串
 */
export function toISOString(date: any) {
  if (!date) return '';
  return moment(date).toISOString();
}