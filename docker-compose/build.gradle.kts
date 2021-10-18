/**
 * create lifecycle tasks
 */
val globalBuildGroup = "SymCps Deploy"
val ciBuildGroup = "Continuous Integration Build"

//tasks.named<TaskReportTask>("tasks") {
//    displayGroup = globalBuildGroup
//}
//
//tasks.register("qualityCheckAll") {
//    group = globalBuildGroup
//    dependsOn(subprojects.map { ":${it.name}:qualityCheck"})
//}
//
//tasks.register("checkAll") {
//    group = ciBuildGroup
//    dependsOn(subprojects.map { ":${it.name}:check"})
//}