set PATH=%PATH%;../../third/bin/curl

del /f /q origin\*

curl.exe -o origin\wowo.beijing.xml "http://www.55tuan.com/partner/partnerApi?partner=wowo&city=beijing"

curl.exe -o origin\meituan.beijing.xml "http://www.meituan.com/api/v2/beijing/deals"

curl.exe -o origin\lashou.beijing.xml "http://open.lashou.com/opendeals/lashou/2419.xml"

curl.exe -o origin\nuomi.beijing.xml "http://www.nuomi.com/api/dailydeal?version=v1&city=beijing"

pause