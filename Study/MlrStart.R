library(mlr) 
library(mlbench) 

# create a task  
mlrTask = makeClassifTask(data = iris, target = "Species") 
lrn = makeLearner("classif.rpart") 
print(lrn) 
getParamSet(lrn) 

# run cv 
cv3f = makeResampleDesc("CV", iters = 3, stratify = TRUE) 
cv10f = makeResampleDesc("CV", iters = 10) 
measures = list(mmce, acc) 
resample(lrn, mlrTask, cv10f, measures)$aggr 

data("Sonar", package = "mlbench") 

tasks = list(
    makeClassifTask(data = iris, target = "Species"),
    makeClassifTask(data = Sonar, target = "Class") 
) 

learners = list(
    makeLearner("classif.rpart"), 
    makeLearner("classif.randomForest"), 
    makeLearner("classif.ksvm") 
) 

benchmark(learners, tasks, cv10f, mmce) 
