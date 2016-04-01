oldwd <- getwd() 
setwd("/home/mikhail/Desktop/GitProjects/FrugalML") 
files <- list.files() 

# features of a data set     
qualities <- read.csv("openml_data_qualities.csv") 
separate_qualities <- split(qualities, qualities$dataset) 
dataSize <- qualities[qualities$quality == "InstanceCount", ] 
dataSize <- dataSize[order(dataSize$value, decreasing = TRUE), ] 

# results of algorithms 
evaluations <- read.csv("openml_evaluations_all.csv") 
subEval <- evaluations[1:800, ] 
separate_evaluations <- split(evaluations, evaluations$task_id) 

# save names of data sets for further analysis 
allMeasures <- data.frame() 

# new map for storing values   
map <- new.env(hash=T, parent=emptyenv()) 

# selects data sets by size   
filterSize <- FALSE  

if (filterSize) {
    instanceCount <- 1000
    dataSize <- dataSize[dataSize$value >= instanceCount, ] 
} else {
    instanceCount <- "all"  
}

processedSet <- 0  

for (i in 1: length(separate_evaluations)) {
    
    # make a copy of set 
    x <- as.data.frame(separate_evaluations[i]) 
    x <- setNames(x, c("task_id", "dataset", "algo", "error_message", "accuracy", "auroc", "training_millis", "testing_millis", "confusion_matrix")) 
    
    # receive name 
    xName <- as.character(x$dataset[1])
    allMeasures[i, 1] = xName    
    
    if (xName %in% dataSize$dataset) { 
        
        processedSet <- processedSet + 1 
        
        print("yes") 
        
        if (i == 1) {
            algorithms <- x$algo 
            shortNames <- strtrim(algorithms, 5) 
        }
        
        # change type from factor to numeric and remove NA values 
        x$auroc <- as.numeric(as.character(x$auroc))
        x <- x[!is.na(x$auroc),] 
        
        # convert time variable to numbers 
        x$training_millis <-
            as.numeric(as.character(x$training_millis))
        x$testing_millis <-
            as.numeric(as.character(x$testing_millis))
        
        # check if training time in milliseconds and change to seconds
        x <- x[!is.na(x$training_millis),]
        if (max(x$training_millis > 1000000)) {
            x$training_millis <- x$training_millis / 1000
        }
        x <- x[x$training_millis < 100000,]
        
        # check if testing time in milliseconds and change for seconds
        x <- x[!is.na(x$testing_millis),]
        if (max(x$testing_millis > 1000000)) {
            x$testing_millis <- x$testing_millis / 1000
        }
        x <- x[x$testing_millis < 100000,]
        
        # caclulate and log combined time for each algorithm
        x$combineTime <- log(x$training_millis + x$testing_millis + 1)
        
        # filter columns and save AUC, training time and name of algorithm
        x <- data.frame(x$auroc, x$combineTime, x$algo)
        smallX <- setNames(x, c("AUC", "CombineTime", "Algorithm"))
        smallX <-
            smallX[order(smallX$AUC,smallX$CombineTime,decreasing = TRUE),]
        smallX$AUC <- smallX$AUC * -1
        
        # calculate Pareto front
        paretoFront = smallX[which(!duplicated(cummin(smallX$CombineTime))),] 
        
        # insert new key or update existing one    
        for (alg in 1: nrow(paretoFront)) {
            algName <- as.character(paretoFront[alg, 3])  
            if (is.null(map[[algName]])) { 
                map[[algName]] <- 1 
            } else { 
                frequency <- map[[algName]] + 1 
                map[[algName]] <- frequency   
            } 
        } 
        
        if (processedSet == 1) {
            png(filename= "plots/abc_general.png", width = 2048, height = 1536)  
            #        plot(paretoFront[,1:2], col = rainbow(103) , xlim=c(min(paretoFront$AUC), 0), ylim=c(0,max(paretoFront$TrainTime)), pch = 20, cex = 0.9) 
            plot(paretoFront[,1:2], col = rainbow(103) , xlim=c(min(paretoFront$AUC), -0.9), ylim=c(0,5), pch = 20, cex = 0.9) 
            legend("topright", legend = strtrim(algorithms, 70), col =  rainbow(103) , pch = 20, lty = 1, cex = 0.8, pt.cex = 0.8)    
        }
        else { 
            #        points(paretoFront[,1:2], col = rainbow(103) , xlim=c(min(paretoFront$AUC), 0), ylim=c(0,max(paretoFront$TrainTime)), pch = 20, cex = 0.9)  
            
            points(paretoFront[,1:2], col = rainbow(103) , xlim=c(min(paretoFront$AUC), -0.9), ylim=c(0, 5), pch = 20, cex = 0.9) 
            text(paretoFront[,1:2], labels = substr(paretoFront[, 3], 0, 4), cex = 0.7, pos = 3 ) 
        } 
        
    }
    
} 

dev.off() 

getValue <- function(x) {
    map[[x]] 
} 

# find all keys in map   
kys <- ls(map)

# create data frame 
algValues <- data.frame(); 
for (i in 1:length(kys)) {
    algValues[i, 1] <- kys[i]
    algValues[i, 2] <- as.character(getValue(kys[i])) 
} 
algValues$V2 <- as.numeric(algValues$V2) 
algValues <- algValues[order(algValues$V2, decreasing = TRUE), ] 
algValues <- setNames(algValues, c("Algorithm", "Number of times on Pareto front")) 

if (filterSize) { 
    write.csv(algValues, paste("selectedSets", instanceCount, ".csv"))  
} else { 
    write.csv(algValues, "allSets.csv") 
}     

# clean map     
rm(map)  

# store in arff format for external processing  
writeArff <- FALSE 
if (writeArff) {
    library(foreign)
    write.arff(tds, "tactivity.arff", "\n", deparse(substitute(tds))) 
} 

setwd(oldwd) 

