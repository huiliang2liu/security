./gradlew security:assembleDebug
./gradlew security:publishPluginPublicationToNexusRepository
p=`pwd`
cd /Users/liuhuiliang/work/maven/security
echo ".DS_Store" > .gitignore
git add .
git commit -m 这是测试
git push gitee master
cd $p