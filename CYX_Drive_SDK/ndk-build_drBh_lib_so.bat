echo off

echo *********************************************************
echo * befor build, copy files.
echo ---------------------------------------------------------

mkdir bin\armeabi
mkdir bin\x86


echo copy libs\armeabi\libBaiduMapSDK_v2_4_1.so bin\armeabi\libBaiduMapSDK_v2_4_1.so 
copy libs\armeabi\libBaiduMapSDK_v2_4_1.so bin\armeabi\libBaiduMapSDK_v2_4_1.so 

echo copy libs\armeabi\liblocSDK4b.so bin\armeabi\liblocSDK4b.so 
copy libs\armeabi\liblocSDK4b.so bin\armeabi\liblocSDK4b.so 


echo *********************************************************
echo * ndk-build_drBh_lib so
echo ---------------------------------------------------------

call ndk-build %1

echo *********************************************************
echo * after build, copy files.
echo ---------------------------------------------------------


echo copy bin\armeabi\libBaiduMapSDK_v2_4_1.so libs\armeabi\libBaiduMapSDK_v2_4_1.so 
copy bin\armeabi\libBaiduMapSDK_v2_4_1.so libs\armeabi\libBaiduMapSDK_v2_4_1.so 

echo copy bin\armeabi\libBaiduMapSDK_v2_4_1.so libs\armeabi\libBaiduMapSDK_v2_4_1.so 
copy bin\armeabi\liblocSDK4b.so libs\armeabi\liblocSDK4b.so

echo *********************************************************
echo * complete.
echo ---------------------------------------------------------

pause

