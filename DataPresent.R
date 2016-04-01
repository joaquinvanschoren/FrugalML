oldwd <- getwd() 
setwd("/home/mikhail/Desktop/GitProjects/FrugalML") 
files <- list.files() 

# features of a data set     
qualities <- read.csv("openml_data_qualities.csv") 
separate_qualities <- split(qualities, qualities$dataset) 

# results of algorithms 
evaluations <- read.csv("openml_evaluations_all.csv") 
subEval <- evaluations[1:800, ] 
separate_evaluations <- split(evaluations, evaluations$task_id) 

# save names of data sets for further analysis 
allMeasures <- data.frame() 

for (i in 1: length(separate_evaluations)) {

    # make a copy of set 
    x <- as.data.frame(separate_evaluations[i]) 
    x <- setNames(x, c("task_id", "dataset", "algo", "error_message", "accuracy", "auroc", "training_millis", "testing_millis", "confusion_matrix")) 

    # receive name 
    xName <- as.character(x$dataset[1])
    allMeasures[i, 1] = xName    
        
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
    x$combinedTime <- log(x$training_millis + x$testing_millis + 1)
    
    # filter columns and save AUC, training time and name of algorithm
    x <- data.frame(x$auroc, x$combinedTime, x$algo)
    smallX <- setNames(x, c("AUC", "CombinedTime", "Algorithm"))
    smallX <-
        smallX[order(smallX$AUC,smallX$CombinedTime,decreasing = TRUE),]
    smallX$AUC <- smallX$AUC * -1
    
    # calculate Pareto front
    paretoFront = smallX[which(!duplicated(cummin(smallX$CombinedTime))),] 
    
    # save on disk in png format    
    png(filename= paste("plots/items/", xName, ".png", sep = ""), width = 640, height = 480) 
    plot(paretoFront[,1:2], col = paretoFront$Algorithm, xlim=c(min(paretoFront$AUC), 0), ylim=c(0,max(paretoFront$CombinedTime)), pch = 20, cex = 1.5) 
    title(main = xName)
    legend("topright", legend = strtrim(paretoFront$Algorithm, 70), col = paretoFront$Algorithm, pch = 20, lty = 1, cex = 0.8, pt.cex = 1.2)  
    dev.off() 
} 

# store in arff format for external processing  
writeArff <- FALSE 
if (writeArff) {
    library(foreign)
    write.arff(tds, "tactivity.arff", "\n", deparse(substitute(tds))) 
} 

setwd(oldwd) 
