library(tidyr)   

source("SciCl.R") 

set.seed(7L) 

loadQualities <- function() {
    qualities <- read.csv("data/openml_data_qualities.csv") 
    
    return (qualities) 
} 
  
computeMeanForClusters <- function(x) { 
    options("scipen" = 100, "digits" = 4) 
    sbc <- data.frame(rep(NA, length(x)))
    for (i in 1:length(x)) {
        sbx_a <- x[[i]] 
        sbx_a <- as.data.frame(apply(X = sbx_a, 2, mean)) 
        colnames(sbx_a) <- paste("Cluster_", i, sep = "") 
        sbc <- cbind(sbc, sbx_a) 
    }
    sbc <- sbc[ , -1] 
    return (sbc) 
} 

qualities <- loadQualities() 
data_wide <- spread(data = qualities, key = 'quality', value = 'value')   

# remove features with many empty elements 
emptyValuesColumns <- c() 
for (i in 1:ncol(data_wide)) { 
    emptiesInColumn <- sum(is.na(data_wide[, i])) 
    emptyValuesColumns <- c(emptyValuesColumns, emptiesInColumn)  
} 
skipColumns <- emptyValuesColumns > (nrow(data_wide) / 2) 
data_wide <- data_wide[ , !skipColumns] 

# fill missing values 
imputeData <- imputeEmptyVals(data_wide[, 2:ncol(data_wide)]) 

rownames(imputeData) <- data_wide$dataset 

if1 <- grep("Dimensionality", names(imputeData)) 
if2 <- grep("NumberOfClasses", names(imputeData)) 
if3 <- grep("NumAttributes", names(imputeData)) 
if4 <- grep("ClassEntropy", names(imputeData)) 
if5 <- grep("PercentageOfMissingValues", names(imputeData)) 
if6 <- grep("DecisionStumpAUC", names(imputeData)) 
if7 <- grep("MeanMeansOfNumericAtts", names(imputeData)) 
if8 <- grep("MaxNominalAttDistinctValues", names(imputeData)) 
if9 <- grep("MajorityClassSize", names(imputeData)) 
if10 <- grep("NumberOfNumericFeatures", names(imputeData)) 
if11 <- grep("NumberOfFeatures", names(imputeData)) 
data_wide <- imputeData[ , c(if1, if2, if3, if4, if5, if6, if7, if8, if9, if10, if11)] 

# search for the optimal number of clusters 
for (ocl in 2:11) {
    features_pair <- combn(names(data_wide)[1:ncol(data_wide)], ocl, simplify = FALSE) 

    maxSilValue <- 0 
    ti <- 0
    for(pair in features_pair) {
        sbx_ps <- data_wide[,  pair] 
        time.scaled <- scale(sbx_ps)  
        k.max <- 12
        sil <- rep(0, k.max)
        
        # Compute the average silhouette width for 
        # k = 2 to k = 15
        for(i in 2:k.max){
            km.res <- kmeans(time.scaled, centers = i, nstart = 25, iter.max = 10)
            ss <- silhouette(km.res$cluster, dist(time.scaled))     
            sil[i] <- mean(ss[, 3])
        }
        
        maxValue <- max(sil) 
        n <- length(sil) 
        secMaxValue <- sort(sil,partial=n-1)[n-1] 
        
        if (maxValue > 0.7 & which.max(sil) > 5 & (maxValue - secMaxValue) > 0.01 ) { 
            maxSilValue <- maxValue
            print(paste(pair, " have value ", maxSilValue, " and i ", ti + 1, sep = "")) 
            
            # Plot the  average silhouette width
            plot(1:k.max, sil, type = "b", pch = 19, frame = FALSE, xlab = "Number of clusters k")
            abline(v = which.max(sil), lty = 2) 
        } 
    }     
} 
  
selectedValues <- c(if2, if3, if5, if8, if9)  
imputeData <- imputeData[, selectedValues] 
cleanData <- as.data.frame(scale(imputeData)) 

# use cluster analysis
numDataSets <- drawSilhouette(p.matrix = cleanData, p.k = 50L)  

# get clusters for data sets with kMeans algorithm
clusters <- getkMeansClusters(p.matrix = cleanData, p.numClusters = numDataSets) 
table(clusters) 

cleanData <- cbind(imputeData, clusters)  

cleanData <- split(x = cleanData, f = as.factor(cleanData$clusters))  

commonData <- computeMeanForClusters(cleanData) 


noisyData <- c("475_analcatdata_germangss", "848_schlvote", 
               "737_space_ga", "453_analcatdata_bondrate", 
               "998_analcatdata_bondrate", "1046_mozilla4", "938_sleuth_ex1221", 
               "1002_kdd_ipums_la_98-small", "836_sleuth_case1102", "735_cpu_small", 
               "759_analcatdata_olympic2000", "938_sleuth_ex1221", "1002_kdd_ipums_la_98-small",
               "1018_kdd_ipums_la_99-small", "179_adult", "311_oil_spill", 
               "761_cpu_act", "993_kdd_ipums_la_97-small", "947_arsenic-male-bladder", 
               "951_arsenic-male-lung", "950_arsenic-female-lung")   






