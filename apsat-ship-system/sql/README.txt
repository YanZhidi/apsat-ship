1、先创建数据库 apsat-ship
2、依次执行0-INIT.sql、1-apsat-ship-structure.sql
3、执行2-apsat-ship-data.sql为船舶同步数据，执行耗时较长，若不执行该条 sql 则需要修改 t_ship 表last_device_stime、last_detail_stime为相应同步起始时间并清空last_detail_id字段值，定时任务则会自动同步该时间之后的数据