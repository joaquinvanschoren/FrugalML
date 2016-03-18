oldwd <- getwd()

# set your own directory 
setwd("/home/mikhail/Desktop/GitProjects/FrugalML") 
 
# results of algorithms
evaluations <- read.csv("data/openml_evaluations_all.csv") 
separate_evaluations <- split(evaluations, evaluations$task_id) 

source("HugeMatrixFunctions.R") 

# find all algrothms from all results
algorithms <- data.frame(levels(evaluations$algo)) 
 
# prepare matrix with filling in the names of algorithms as column names
hugeMatrix <- data.frame(t(algorithms)) 
colnames(hugeMatrix) <- algorithms[1:103, 1]
hugeMatrix <- hugeMatrix[-1,]

# store the number of algorithms in all items
numOfAlgs <- dim(hugeMatrix)[2]

# prepare names of algorithms for processing
colnames(algorithms) <- "algorithm"
algorithms$algorithm <- as.character(algorithms$algorithm)

# create short names
for (i in 1:numOfAlgs) {
    algFullName <- as.character(algorithms[i, 1])
    algorithms[i, 1] <- shortNameAlgorithm(algFullName)
}
algorithms <- data.frame(t(algorithms))

# change from factor to character
algorithms[] <- lapply(algorithms, as.character)

# replace long names with short names
colnames(hugeMatrix) <- algorithms[1,] 

# calculate the number of all sets in files
quantityDataSets <- length(separate_evaluations)

for (i in 1:quantityDataSets) {
    # make a copy of set
    dataSet <- as.data.frame(separate_evaluations[i])

    processedDataSet <- originalScoreData(hugeMatrix, dataSet)
    
    # add results to the matrix
    hugeMatrix <- rbind(hugeMatrix, processedDataSet[[2]]) 
} 

# explore the number of missing values for data sets  
dataSetMissingValues <- data.frame() 
for (i in 1:quantityDataSets) { 
    sumMissing <- sum(is.na(hugeMatrix[i, ])) 
    dataSetMissingValues <- rbind(dataSetMissingValues, data.frame(rownames(hugeMatrix)[i], sumMissing)) 
} 
dataSetMissingValues <- dataSetMissingValues[order(dataSetMissingValues$sumMissing, decreasing = TRUE), ] 

# find the number of missing values for algorithms 
algsMissingValues <- data.frame() 
for (i in 1:numOfAlgs) { 
    sumMissing <- sum(is.na(hugeMatrix[, i])) 
    algsMissingValues <- rbind(algsMissingValues, data.frame(colnames(hugeMatrix)[i], sumMissing)) 
} 
algsMissingValues <- algsMissingValues[order(algsMissingValues$sumMissing, decreasing = TRUE), ] 

# select algorithms with more than half of missing values   
limit <- quantile(algsMissingValues$sumMissing, 0.8) 
topAlgorithmsMissingValues <- algsMissingValues[algsMissingValues[ , 2] > limit, ] 

# find all algorithms with missing values   
topAlgorithmsMissingValues <- algsMissingValues[algsMissingValues[ , 2] > 0, ] 

# delete them from a matrix   
hugeMatrix <- hugeMatrix[, !colnames(hugeMatrix) %in% topAlgorithmsMissingValues[, 1]] 

# replace all NA values with negative value
processedValues[is.na(processedValues)] <- -1 

saveRDS(hugeMatrix, "objects/hugeMatrixOnlyWithRealValues.rds") 

setwd(oldwd) 

# compute matrix with pareto front and chosen algorithms 

# for (i in 1:numOfSets) {
#     # make a copy of set
#     x <- as.data.frame(separate_evaluations[i])
#     x <-
#         setNames(
#             x, c(
#                 "task_id", "dataset", "algo", "error_message", "accuracy", "auroc", "training_millis", "testing_millis", "confusion_matrix"
#             )
#         )
#     
#     # receive name 
#     xName <- as.character(x$dataset[1])
#     allMeasures[i, 1] = xName    
#     
#     # change type from factor to numeric and remove NA values 
#     x$auroc <- as.numeric(as.character(x$auroc))
#     x <- x[!is.na(x$auroc),] 
#     
#     # convert time variable to numbers 
#     x$training_millis <-
#         as.numeric(as.character(x$training_millis))
#     x$testing_millis <-
#         as.numeric(as.character(x$testing_millis))
#     
#     # check if training time in milliseconds and change to seconds
#     x <- x[!is.na(x$training_millis),]
#     #     if (max(x$training_millis > 1000000)) {
#     #         x$training_millis <- x$training_millis / 1000
#     #     }
#     #     x <- x[x$training_millis < 100000,]
#     
#     # check if testing time in milliseconds and change for seconds
#     x <- x[!is.na(x$testing_millis),]
#     #     if (max(x$testing_millis > 1000000)) {
#     #         x$testing_millis <- x$testing_millis / 1000
#     #     }
#     #     x <- x[x$testing_millis < 100000,]
#     
#     # caclulate and log combined time for each algorithm
#     x$combineTime <- log(x$training_millis + x$testing_millis + 1)
#     
#     # filter columns and save AUC, training time and name of algorithm
#     x <- data.frame(x$auroc, x$combineTime, x$algo)
#     smallX <- setNames(x, c("AUC", "CombineTime", "Algorithm"))
#     smallX <-
#         smallX[order(smallX$AUC,smallX$CombineTime,decreasing = TRUE),]
# 
#     smallX$AUC <- smallX$AUC * -1 
#     
#     # calculate Pareto front
#     paretoFront = smallX[which(!duplicated(cummin(smallX$CombineTime))),] 
#     
#     # create a vector of values for this set 
#     paretoRes <- t(data.frame(rep(NA, numOfAlgs)))  
#     colnames(paretoRes) <- colnames(hugeMatrix)  
#     rownames(paretoRes) <- xName 
#     
#     # update value if an algorithm at the Pareto front 
#     for (algValue in 1:nrow(paretoFront)) { 
#     
#         algName <- as.character(smallX[algValue, 3])
#         
#         algIndex <- grep(pattern = substr(algName, 0, 5), x = colnames(paretoRes), ignore.case = TRUE) 
#         paretoRes[1, algIndex] <- 1 
#     } 
# 
#     # add results to the matrix    
#     hugeMatrix <- rbind(hugeMatrix, paretoRes) 
# } 
 
