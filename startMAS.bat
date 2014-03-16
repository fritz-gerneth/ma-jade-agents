java jade.Boot -gui -jade_domain_df_autocleanup true -jade_core_management_AgentManagementService_agentspath ./target fakeGpsDevice:de.effms.jade.agent.gpsfakedevice.GpsFakeDevice -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:59093,suspend=y,server=n -Dfile.encoding=UTF-8
pause
