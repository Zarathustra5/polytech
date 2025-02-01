module lab6_2 #(parameter W = 20) (
    input [W-1:0] a, b,
    output reg [W-1:0] max, min
);
    function [W-1:0] Fmax;
        input [W-1:0] a, b;
        begin 
            Fmax = (a > b) ? a : b;
        end
    endfunction

    function [W-1:0] Fmin;
        input [W-1:0] a, b;
        begin 
            Fmin = (a < b) ? a : b;
        end
    endfunction
    
    always @* begin
        max = Fmax(a, b);
        min = Fmin(a, b);
    end

endmodule
		