oldwd <- getwd()

# set your own directory 
setwd("/home/mikhail/Desktop/GitProjects/FrugalML") 
 
# results of algorithms
evaluations <- read.csv("data/openml_evaluations_all.csv") 

createHugeMatrix <- function(originData) {  

    separate_evaluations <- split(originData, originData$task_id)
    
    # save names of data sets for further analysis 
    allMeasures <- data.frame() 
    
    # find all algrothms from all results
    douplicatedAlgos <- originData$algo
    algorithms <- data.frame(levels(douplicatedAlgos))
    
    # prepare matrix with filling in the names of algorithms as column names
    hugeMatrix <- data.frame(t(algorithms))
    colnames(hugeMatrix) <- algorithms[1:103, 1]
    hugeMatrix <- hugeMatrix[-1,] 
    
    # store the number of algorithms in all items 
    numOfAlgs <- dim(hugeMatrix)[2] 
    
    # create short names 
    colnames(algorithms) <- "algorithm" 
    algorithms$algorithm <- as.character(algorithms$algorithm) 
    for (i in 1: numOfAlgs) { 
        algFullName<- as.character(algorithms[i, 1])  
        
        posOfEmpty <- gregexpr(pattern =' ', algFullName)[[1]][1] 
        
        if (posOfEmpty > 0) {  
            shortName <- paste(substr(algFullName, 0, 5), substr(algFullName, 23, posOfEmpty - 1), sep = "") 
        } else {
            shortName <- paste(substr(algFullName, 0, 5), substr(algFullName, 23, nchar(algFullName)), sep = "") 
        }
        
        algorithms[i, 1] <- shortName 
    } 
    algorithms <- data.frame(t(algorithms))   
    
    # change from factor to character 
    algorithms[] <- lapply(algorithms, as.character) 
    
    # replace long names with short names 
    colnames(hugeMatrix) <- algorithms[1, ]  
    
    # calculate the number of all sets in files 
    quantityDataSets <- length(separate_evaluations) 
    
    for (i in 1:quantityDataSets) {
        # make a copy of set
        x <- as.data.frame(separate_evaluations[i])
        x <-
            setNames(
                x, c(
                    "task_id", "dataset", "algo", "error_message", "accuracy", "auroc", "training_millis", "testing_millis", "confusion_matrix"
                )
            )
        
        # receive name 
        xName <- as.character(x$dataset[1])
        allMeasures[i, 1] = xName    
        
        # change type from factor to numeric and remove NA values 
        x$auroc <- as.numeric(as.character(x$auroc))
        x <- x[!is.na(x$auroc),] 
        
        # store highest value for AUC in this particular data set  
        highestAUC <- max(x$auroc) 
        
        # convert time variable to numbers 
        x$training_millis <-
            as.numeric(as.character(x$training_millis))
        x$testing_millis <-
            as.numeric(as.character(x$testing_millis))
        
        # check for missing or negative values   
        x <- x[!is.na(x$training_millis),]
        x$training_millis <- abs(x$training_millis) 
        
        x <- x[!is.na(x$testing_millis),] 
        x$testing_millis <- abs(x$testing_millis) 
        
        # caclulate combined time for each algorithm
        x$combineTime <- x$training_millis + x$testing_millis 
        
        # find the lowest combined time in this data set   
        lowestTime <- min(x$combineTime + 1) 
        
        # filter columns and save AUC, training time and name of algorithm
        x <- data.frame(x$auroc, x$combineTime, x$algo)
        smallX <- setNames(x, c("AUC", "CombineTime", "Algorithm"))
        smallX <-
            smallX[order(smallX$AUC,smallX$CombineTime,decreasing = TRUE),]
        
        # create a vector of values for this set 
        paretoRes <- t(data.frame(rep(NA, numOfAlgs)))  
        colnames(paretoRes) <- colnames(hugeMatrix)  
        rownames(paretoRes) <- xName 
        
        # update value for each algorithm and data set       
        for (algValue in 1:nrow(smallX)) { 
            algName <- as.character(smallX[algValue, 3])
            algIndex <- grep(pattern = substr(algName, 0, 5), x = colnames(paretoRes), ignore.case = TRUE) 
            paretoRes[1, algIndex] <- smallX[algValue, 1] - 0.1 * log(1 + smallX[algValue, 2]) 
        } 
 
        if (sum(is.na(paretoRes) > 0)) {
                
        }
               
        # replace all NA values with negative value 
        paretoRes[is.na(paretoRes)] <- -1  
        
        # add results to the matrix    
        hugeMatrix <- rbind(hugeMatrix, paretoRes)  
    } 
    
    return (hugeMatrix)  
} 

hugeMatrix <- createHugeMatrix(evaluations) 

write.csv(hugeMatrix, "hugeMatrix.csv") 

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
 
