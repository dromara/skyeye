/**
 * 加法
 *
 * @param num1
 * @param num2
 * @returns {string}
 */
function sum(num1, num2){
    var a1 = parseFloat(isNull(num1) ? 0 : num1);
    var a2 = parseFloat(isNull(num2) ? 0 : num2);
    return (a1 + a2).toFixed(2);
}

/**
 * 减法
 *
 * @param num1
 * @param num2
 * @returns {string}
 */
function subtraction(num1, num2){
    var a1 = parseFloat(isNull(num1) ? 0 : num1);
    var a2 = parseFloat(isNull(num2) ? 0 : num2);
    return (a1 - a2).toFixed(2);
}

/**
 * 乘法
 *
 * @param num1
 * @param num2
 * @returns {string}
 */
function multiplication(num1, num2){
    var a1 = parseFloat(isNull(num1) ? 0 : num1);
    var a2 = parseFloat(isNull(num2) ? 0 : num2);
    return (a1 * a2).toFixed(2);
}

/**
 * 除法
 *
 * @param num1
 * @param num2
 * @returns {string|number}
 */
function division(num1, num2){
    var a1 = parseFloat(isNull(num1) ? 0 : num1);
    var a2 = parseFloat(isNull(num2) ? 0 : num2);
    if(a2 == 0){
        return 0;
    }
    return (a1 / a2).toFixed(2);
}
