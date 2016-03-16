library("mlr")
library("OpenML")

mlr::configureMlr(show.info = FALSE) 

tasks <-  subset(tasksSel, NumberOfFeatures > 2 &
                      NumberOfInstances < 100000  & NumberOfClasses == 2 &
                      NumberOfMissingValues == 0, select = task.id, drop = TRUE)  

tasks <- sample(tasks, 20) 

lrn = makeLearner("classif.ada", predict.type="prob") 

ps = makeParamSet(
    makeIntegerParam("iter", lower = 1, upper = 10), 
    makeDiscreteParam("type", values = c("discrete", "real")), 
    makeNumericParam("nu", lower = 1, upper = 2)
) 

ctrl = makeTuneControlGrid(resolution = 2L) 

rdesc = makeResampleDesc("CV", iters = 10L) 

measures = c("predictive_accuracy", "build_cpu_time") 

allResults <- data.frame()  

size <- length(tasks) 

for ( i in 1:size ) {
    task <- getOMLTask(task.id = tasks[i]) 
    dname <- task$input$data.set$desc$name 
    task$input$evaluation.measures = measures 
    task <- convertOMLTaskToMlr(task)$mlr.task 
    res = tuneParams("classif.ada", task, rdesc, par.set = ps, control = ctrl) 
    currentRes <- data.frame(lrn$name, dname, tasks[i], res$x, res$y) 
    allResults <- rbind(allResults, currentRes) 
} 








# learners = listLearners("classif", create = TRUE, warn.missing.packages = FALSE, 
#                         properties = c("prob", "multiclass"))  

# task1$input$estimation.procedure$parameters$number_folds = 10  

# remove qda 
# learners.list = learners.list[-12] 

# ps = makeParamSet(
#     makeNumericParam("C", lower = -12, upper = 12, trafo = function(x) 2^x),
#     makeDiscreteParam("kernel", values = c("vanilladot", "polydot", "rbfdot")),
#     makeNumericParam("sigma", lower = -12, upper = 12, trafo = function(x) 2^x,
#                      requires = quote(kernel == "rbfdot")),
#     makeIntegerParam("degree", lower = 2L, upper = 5L,
#                      requires = quote(kernel == "polydot"))
# )
# print(ps) 

# running Learners
# aux = lapply(learners[12:14], function(lrnr){
#     obj = runTaskMlr(task = task, learner = lrnr) 
#     
#     # learner.id = uploadOMLFlow(lrnr) 
#     # run.id = uploadOMLRun(obj) 
# 
#     return(obj)
# }) 

# Summarizing results
# df = do.call("rbind", lapply(aux, 
#                              function(elem){ 
#                                  return(getBMRAggrPerformances(elem$mlr.benchmark.result, as.df = TRUE))
#                              }
# )) 

# print(head(as.data.frame(res$opt.path)))  





