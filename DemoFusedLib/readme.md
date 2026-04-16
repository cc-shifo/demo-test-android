# 运行发布到本地目录任务
```
./gradlew :fusedlib:publishRegisterPublicationToLocalaarRepository
之后，可以在当前模块的 `build/build_repo/my-company/my-fused-library/1.0` 目录下找到发布到本地目录的aar文件
```

# 运行发布到本地maven仓库，在~/.m2/repository/目录下找到发布到本地maven仓库的aar文件
# 例如C:\Users\Administrator\.m2\repository\my-company\my-fused-library\1.0\my-fused-library-1.0.aar
```
./gradlew :fusedlib:publishRegisterPublicationToMavenLocal
```