module lab5_1 (
    input clk,            
    input srst_in,       
    input [3:0] din,    
    output reg [7:0] q  
);

reg [7:0] state;
reg srst;          
reg [3:0] d;     

parameter S0 = 0, S1 = 1, S2 = 2;

always @(posedge clk) begin
    srst <= srst_in;  
    d <= din;         
end

always @(posedge clk) begin
    if (srst == 0) begin
        state <= S0;  
    end else begin
        case (state)
            S0: begin
                if (d == 4'h1) state <= S1;  
                else state <= S0; 
            end
            S1: begin
                if (d == 4'h2) state <= S2;
                else state <= S1; 
            end
            S2: begin
                if (d == 4'h4) state <= S1;
					else if (d == 4'h8) state <= S0; 
                else state <= S2; 
            end
        endcase
    end
end

always @(state) begin
    case (state)
        S0: q = 8'h00;
        S1: q = 8'h55;
        S2: q = 8'hff;
        default: q = 8'h00;
    endcase
end

endmodule
