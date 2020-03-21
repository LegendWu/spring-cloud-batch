##JOB测试数据
INSERT INTO BATCH_JOB (JOB_NAME, JOB_CODE, CRON, JOB_DESC, STATUS) VALUES ('1', '早间批', 'daily_batch', '0/15 * * * * ?', '1', '早上6:00-8:00执行', '', null, 'OPEN');
INSERT INTO BATCH_JOB VALUES ('2', '晚间批', 'night_batch', '0 0 22 * * ?', '2', '晚上22:00-1:00执行', null, null, 'CLOSE');

UPDATE JOB_ENTITY SET CRON = '0/2 * * * * ?' WHERE ID=1;