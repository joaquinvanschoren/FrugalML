# ensure the results are repeatable
set.seed(7) 

# enable parallel processing where applicable 
library(doMC) 
registerDoMC(cores=4) 

# load the library
library(mlbench)  
library(caret)  

# calculate correlation matrix
correlationMatrix <- round(cor(dsa[,1:21]), 2)  

# summarize the correlation matrix
print(correlationMatrix) 

# find attributes that are highly corrected (ideally >0.75)
highlyCorrelated <- findCorrelation(correlationMatrix, cutoff = 0.75) 
# print indexes of highly correlated attributes
print(highlyCorrelated) 

rotVecFeatures <- grep("RotVecZ", colnames(dsa)) 
magFieldFeatures <- c(grep("MagFielX", colnames(dsa)), grep("MagFielZ", colnames(dsa))) 
dsa <- dsa[ , -c(rotVecFeatures, magFieldFeatures)] 

# prepare training scheme
control <- trainControl(method = "repeatedcv", number = 10, repeats = 1)  
# train the model
model <- train(Activity ~ ., data = dsa, method = "lvq", preProcess = "scale", trControl = control) 
# estimate variable importance
importance <- varImp(model, scale=FALSE)
# summarize importance
print(importance) 
# plot importance
plot(importance)

# define the control using a random forest selection function
control <- rfeControl(functions = rfFuncs, method = "cv", number = 3) 
# run the RFE algorithm
results <- rfe(dsa[1:10000, 1:18], dsa[1:10000, 19], sizes = c(1:18), rfeControl = control)
# summarize the results
print(results)
# list the chosen features
predictors(results)
# plot the results
plot(results, type=c("g", "o")) 
 